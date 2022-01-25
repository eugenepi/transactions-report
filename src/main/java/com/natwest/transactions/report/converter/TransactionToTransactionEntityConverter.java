package com.natwest.transactions.report.converter;

import com.natwest.transactions.report.entities.TransactionEntity;
import com.natwest.transactions.report.model.Transaction;

public class TransactionToTransactionEntityConverter {

    public TransactionEntity toEntity(Transaction transaction) {
        return new TransactionEntity(transaction.getId(), transaction.getCurrency(), transaction.getDate(),
        transaction.getIban(), transaction.getDescription(), transaction.getAmount());
    }

    public Transaction toDto(TransactionEntity transactionEntity) {
        return new Transaction(transactionEntity.getTransactionId(), transactionEntity.getCurrency(),
                transactionEntity.getAmount(), transactionEntity.getIban(), transactionEntity.getDate(),
                transactionEntity.getDescription());
    }
}
