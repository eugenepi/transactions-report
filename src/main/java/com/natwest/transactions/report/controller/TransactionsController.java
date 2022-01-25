package com.natwest.transactions.report.controller;

import com.natwest.transactions.report.exceptions.AbsentValueException;
import com.natwest.transactions.report.exceptions.BuildViewException;
import com.natwest.transactions.report.model.TransactionView;
import com.natwest.transactions.report.sevices.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/transactions/")
public class TransactionsController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("{iban}/{currency}/{page}")
    public ResponseEntity<TransactionView> getAccountTransactions(
            @PathVariable(value = "iban") String iban,
            @PathVariable(value = "currency") String currency,
            @PathVariable(value = "page") Integer page) throws Exception {

        if (!StringUtils.hasText(iban)) {
            throw new AbsentValueException("Iban can't be empty");
        }

        if (!StringUtils.hasText(currency)) {
            throw new AbsentValueException("Currency can't be empty");
        }

        if (page < 1) {
            throw new AbsentValueException("Page can't be less than 1");
        }
        TransactionView view;

        try {
            view = this.transactionService.buildTransactionView(iban, currency, page);
        } catch (Exception e) {
            throw new BuildViewException("Can't build view. Reason is :" + e);
        }
        return ResponseEntity.ok(view);


    }


}
