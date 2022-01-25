package com.natwest.transactions.report.controller;

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
    public ResponseEntity<?> getAccountTransactions(
            @PathVariable(value = "iban") String iban,
            @PathVariable(value = "currency") String currency,
            @PathVariable(value = "page") Integer page) {

        if (!StringUtils.hasText(iban)) {
            return ResponseEntity.badRequest().body("Iban can't be empty");
        }

        if (!StringUtils.hasText(currency)) {
            return ResponseEntity.badRequest().body("Currency can't be empty");
        }

        if (page < 1) {
            return ResponseEntity.badRequest().body("Page can't be less than 1");
        }
        TransactionView view;

        try {
            view = this.transactionService.buildTransactionView(iban, currency, page);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Can't build view. Reason is :" + e);

        }
        return ResponseEntity.ok(view);


    }


}
