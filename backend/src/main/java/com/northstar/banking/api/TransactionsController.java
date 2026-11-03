package com.northstar.banking.api;

import com.northstar.banking.application.CoreBankingService;
import com.northstar.banking.model.BankingModels.Transaction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TransactionsController {
    private final CoreBankingService core;

    public TransactionsController(CoreBankingService core) {
        this.core = core;
    }

    @GetMapping("/api/core/transactions")
    public List<Transaction> transactions() {
        return core.transactions();
    }
}
