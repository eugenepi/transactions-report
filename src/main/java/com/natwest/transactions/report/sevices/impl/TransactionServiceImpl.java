package com.natwest.transactions.report.sevices.impl;

import com.natwest.transactions.report.converter.TransactionToTransactionEntityConverter;
import com.natwest.transactions.report.entities.PagesView;
import com.natwest.transactions.report.entities.TransactionEntity;
import com.natwest.transactions.report.exceptions.ExchangeRateException;
import com.natwest.transactions.report.exceptions.NoTransactionsException;
import com.natwest.transactions.report.exceptions.WrongPageNumberException;
import com.natwest.transactions.report.model.Transaction;
import com.natwest.transactions.report.model.TransactionView;
import com.natwest.transactions.report.repositories.TransactionRepository;
import com.natwest.transactions.report.sevices.TransactionService;
import com.natwest.transactions.report.sevices.CurrencyExchangeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionToTransactionEntityConverter converter;
    private final CurrencyExchangeService currencyExchangeService;

    private final Logger logger = LogManager.getLogger(TransactionServiceImpl.class);

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  TransactionToTransactionEntityConverter converter,
                                  CurrencyExchangeService currencyExchangeService) {
        this.transactionRepository = transactionRepository;
        this.converter = converter;
        this.currencyExchangeService = currencyExchangeService;
    }

    @Override
    public void saveAll(List<Transaction> transactionsList) {
        List<TransactionEntity> sensorDataEntityList = transactionsList
                .stream()
                .map(this.converter::toEntity)
                .collect(Collectors.toList());
        if (!sensorDataEntityList.isEmpty()) {
            this.transactionRepository.saveAll(sensorDataEntityList);
        }
    }

    @Override
    public TransactionView buildTransactionView(String iban, String currency, Integer page) throws Exception {
        List<PagesView> pagesViewList = this.transactionRepository.getPagesView(iban);

        if (pagesViewList.isEmpty()) {
            throw new NoTransactionsException("Can't find transactions for iban " + iban);
        }

        if (page > pagesViewList.size()) {
            logger.warn("Wrong page number. Possible page from 1 to "  + pagesViewList.size());
            throw new WrongPageNumberException("Wrong page number. Possible page from 1 to " + pagesViewList.size());
        }

        PagesView pageView = pagesViewList.get(page - 1);
        String year = pageView.getDate().split("-")[0];
        String month = pageView.getDate().split("-")[1];

        List<TransactionEntity> transactionEntities
                = this.transactionRepository.getTransactionsByDateAndIban(iban, year, month);

        Optional<Map<String, Double>> exchangeRate = this.currencyExchangeService.getExchangeRate(currency);

        if (exchangeRate.isPresent()) {
            Map<String, Double> rate = exchangeRate.get();
            BigDecimal debit = BigDecimal.ZERO;
            BigDecimal credit = BigDecimal.ZERO;

            for (TransactionEntity transaction : transactionEntities) {
                BigDecimal amount = transaction.getAmount();
                if (!transaction.getCurrency().equals(currency)) {
                    Double currencyRate = rate.get(transaction.getCurrency());
                    amount = transaction.getAmount().multiply(new BigDecimal(currencyRate));
                }
                if (amount.compareTo(BigDecimal.ZERO) > 0) {
                    debit = debit.add(amount);
                } else {
                    credit = credit.add(amount.abs());
                }
            }

            List<Transaction> transactionList = transactionEntities
                    .stream()
                    .map(this.converter::toDto)
                    .collect(Collectors.toList());

            return new TransactionView(
                    iban, currency, year, month, page, pagesViewList.size(), credit.setScale(2, RoundingMode.HALF_UP),
                    debit.setScale(2, RoundingMode.HALF_UP), transactionList);

        }
         throw new ExchangeRateException("Can't get result from Exchange rate service.");
    }
}
