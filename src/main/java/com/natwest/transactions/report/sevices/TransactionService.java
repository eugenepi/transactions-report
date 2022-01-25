package com.natwest.transactions.report.sevices;

import com.natwest.transactions.report.model.Transaction;
import com.natwest.transactions.report.model.TransactionView;

import java.util.List;

public interface TransactionService {

    void saveAll(List<Transaction> transactionsList);

    TransactionView buildTransactionView(String iban, String currency, Integer page) throws Exception;
}
