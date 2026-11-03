package com.northstar.banking.api;

import com.northstar.banking.application.TransactionService;
import com.northstar.banking.model.BankingModels.Transaction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TransactionsController {
    private final TransactionService transactionsService;

    public TransactionsController(TransactionService transactionsService) {
        this.transactionsService = transactionsService;
    }

    @GetMapping("/api/core/transactions")
    public List<Transaction> transactions() {
        return transactionsService.transactions();
    }
}
