package com.natwest.integration;

import com.natwest.transactions.report.sevices.CurrencyExchangeService;
import com.natwest.transactions.report.sevices.impl.ExchangeRateApiService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(classes = CurrencyExchangeService.class)
@ActiveProfiles("test")
public class ExchangeRateServiceTest {

  @Value("${exchange_rate.api_key}")
  private String apiKey;

  @Test
  public void responsePresentTest() {
        RestTemplate restTemplate = new RestTemplate();
        CurrencyExchangeService currencyExchangeService = new ExchangeRateApiService(restTemplate, apiKey);
        Assertions.assertTrue(currencyExchangeService.getExchangeRate("USD").isPresent());
    }

    @Test
    public void noResponseTest() {
        RestTemplate restTemplate = new RestTemplate();
        CurrencyExchangeService currencyExchangeService = new ExchangeRateApiService(restTemplate, "test");
        Assertions.assertTrue(currencyExchangeService.getExchangeRate("USD").isEmpty());
    }
}
