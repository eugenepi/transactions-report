package com.natwest.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.natwest.transactions.report.TransactionServiceApplication;
import com.natwest.transactions.report.model.ErrorDto;
import com.natwest.transactions.report.model.Transaction;
import com.natwest.transactions.report.model.TransactionView;
import com.natwest.transactions.report.repositories.TransactionRepository;
import com.natwest.transactions.report.sevices.CurrencyExchangeService;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TransactionServiceApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = "classpath:scripts/init.sql")
public class IntegrationTest extends DockerContainer {

  @Autowired private KafkaTemplate<String, String> kafkaTemplate;

  @Autowired private TransactionRepository repository;

  @Autowired private MockMvc mockMvc;

  @MockBean private CurrencyExchangeService currencyExchangeService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  public void init() throws JsonProcessingException {
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    Map<String, Double> mockExchangeRate = new HashMap<>();
    mockExchangeRate.put("CHF", Double.parseDouble("0.9121"));
    mockExchangeRate.put("EUR", Double.parseDouble("0.8815"));

    Mockito.when(currencyExchangeService.getExchangeRate("USD"))
        .thenReturn(Optional.of(mockExchangeRate));

    List<Transaction> transactionList = createTransactions();

    for (Transaction transaction : transactionList) {
      kafkaTemplate.send(
          "transactions", transaction.getId(), objectMapper.writeValueAsString(transaction));
    }

    Awaitility.await()
        .atMost(Duration.ofSeconds(30))
        .pollDelay(100, TimeUnit.MILLISECONDS)
        .pollInterval(Duration.ofMillis(100))
        .until(() -> repository.findAll().spliterator().estimateSize() == 5);

    Assertions.assertEquals(5, repository.findAll().spliterator().estimateSize());
  }

  @Test
  public void testCorrectRequest() throws Exception {

    if (repository.findAll().spliterator().estimateSize() == 0) {
      init();
    }

    MvcResult mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/transactions/US89770097777/USD/1")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    TransactionView resultView =
        objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TransactionView.class);
    Assertions.assertEquals("US89770097777", resultView.getIban());
    Assertions.assertEquals("USD", resultView.getCurrency());
    Assertions.assertEquals("2021", resultView.getYear());
    Assertions.assertEquals("11", resultView.getMonth());
    Assertions.assertEquals(1, resultView.getPage());
    Assertions.assertEquals(2, resultView.getTotalPages());
    Assertions.assertEquals(
        0, new BigDecimal("50.25").setScale(2).compareTo(resultView.getCredit()));
    Assertions.assertEquals(
        0, new BigDecimal("109.37").setScale(2).compareTo(resultView.getDebit()));
    Assertions.assertEquals(3, resultView.getTransactions().size());
  }

  @Test
  public void testIncorrectRequest() throws Exception {
    if (repository.findAll().spliterator().estimateSize() == 0) {
      init();
    }
    // wrong page number
    MvcResult mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/transactions/US89770097777/USD/5")
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    Assertions.assertEquals(400, mvcResult.getResponse().getStatus());
    Assertions.assertEquals(
        "Can't build view. Reason is :com.natwest.transactions.report.exceptions.WrongPageNumberException:"
            + " Wrong page number. Possible page from 1 to 2",
        objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorDto.class).getMessage());

    // wrong iban
    mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/transactions/US897/USD/1")
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    Assertions.assertEquals(400, mvcResult.getResponse().getStatus());
    Assertions.assertEquals(
        "Can't build view. Reason is :com.natwest.transactions.report.exceptions.NoTransactionsException: "
            + "Can't find transactions for iban US897",
            objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorDto.class).getMessage());

    // wrong currency
    mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/transactions/US89770097777/LOL/1")
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    Assertions.assertEquals(400, mvcResult.getResponse().getStatus());
    Assertions.assertEquals(
        "Can't build view. Reason is :com.natwest.transactions.report.exceptions.ExchangeRateException:"
            + " Can't get result from Exchange rate service.",
            objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorDto.class).getMessage());
  }

  private List<Transaction> createTransactions() {
    List<Transaction> transactionList = new ArrayList<>();

    Transaction transaction1 =
        new Transaction(
            UUID.randomUUID().toString(),
            "USD",
            new BigDecimal("100.33").setScale(2),
            "US89770097777",
            LocalDateTime.ofInstant(
                Instant.parse("2021-11-30T18:35:24.00Z"), ZoneId.systemDefault()),
            "Adding USD 100.33 to account");
    transactionList.add(transaction1);

    Transaction transaction2 =
        new Transaction(
            UUID.randomUUID().toString(),
            "EUR",
            new BigDecimal("10.25").setScale(2),
            "US89770097777",
            LocalDateTime.ofInstant(
                Instant.parse("2021-11-30T20:35:24.00Z"), ZoneId.systemDefault()),
            "Adding EUR 10.25 to account");
    transactionList.add(transaction2);

    Transaction transaction3 =
        new Transaction(
            UUID.randomUUID().toString(),
            "USD",
            new BigDecimal("-50.25").setScale(2),
            "US89770097777",
            LocalDateTime.ofInstant(
                Instant.parse("2021-11-30T22:35:24.00Z"), ZoneId.systemDefault()),
            "Spending USD 50.25 to account");
    transactionList.add(transaction3);

    Transaction transaction4 =
        new Transaction(
            UUID.randomUUID().toString(),
            "CHF",
            new BigDecimal("45.00").setScale(2),
            "US89770097777",
            LocalDateTime.ofInstant(
                Instant.parse("2021-12-01T11:35:24.00Z"), ZoneId.systemDefault()),
            "Adding CHF 45.00 to account");
    transactionList.add(transaction4);

    Transaction transaction5 =
        new Transaction(
            UUID.randomUUID().toString(),
            "CHF",
            new BigDecimal("50.00").setScale(2),
            "US1234567890",
            LocalDateTime.ofInstant(
                Instant.parse("2021-12-01T11:35:24.00Z"), ZoneId.systemDefault()),
            "Adding CHF 50.00 to account");
    transactionList.add(transaction5);

    return transactionList;
  }
}
