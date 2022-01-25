package com.natwest.integration;

import com.natwest.transactions.report.converter.TransactionToTransactionEntityConverter;
import com.natwest.transactions.report.entities.TransactionEntity;
import com.natwest.transactions.report.model.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

public class TransactionConverterTest {

    private final TransactionToTransactionEntityConverter converter
            = new TransactionToTransactionEntityConverter();

    @Test
    public void convertToEntityTest() {
        Transaction transaction = new Transaction(
                UUID.randomUUID().toString(), "USD", new BigDecimal("100.33").setScale(2), "US89770097777",
                LocalDateTime.ofInstant(Instant.parse("2021-11-30T18:35:24.00Z"), ZoneId.systemDefault()),
                "Adding USD 100.33 to account");

        TransactionEntity entity = converter.toEntity(transaction);

        Assertions.assertEquals(transaction.getId(), entity.getTransactionId());
        Assertions.assertEquals(transaction.getCurrency(), entity.getCurrency());
        Assertions.assertEquals(0, entity.getAmount().compareTo(transaction.getAmount()));
        Assertions.assertEquals(transaction.getIban(), entity.getIban());
        Assertions.assertEquals(transaction.getDate(), entity.getDate());
        Assertions.assertEquals(transaction.getDescription(), entity.getDescription());
    }

    @Test
    public void convertToDtoTest() {
        TransactionEntity entity = new TransactionEntity(UUID.randomUUID().toString(), "USD",
                LocalDateTime.ofInstant(Instant.parse("2021-11-30T18:35:24.00Z"), ZoneId.systemDefault()),
                "US89770097777", "Adding USD 100.33 to account",
                new BigDecimal("100.33").setScale(2));

        Transaction transaction = converter.toDto(entity);
        Assertions.assertEquals(transaction.getId(), entity.getTransactionId());
        Assertions.assertEquals(transaction.getCurrency(), entity.getCurrency());
        Assertions.assertEquals(0, entity.getAmount().compareTo(transaction.getAmount()));
        Assertions.assertEquals(transaction.getIban(), entity.getIban());
        Assertions.assertEquals(transaction.getDate(), entity.getDate());
        Assertions.assertEquals(transaction.getDescription(), entity.getDescription());
    }
}
