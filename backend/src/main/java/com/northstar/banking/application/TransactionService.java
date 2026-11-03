package com.northstar.banking.application;

import com.northstar.banking.model.BankingModels.Transaction;
import com.northstar.banking.port.out.CoreBankingPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    private final CoreBankingPort core;

    public TransactionService(CoreBankingPort core) { this.core = core; }

    public List<Transaction> transactions() { return core.dashboard().transactions(); }
}
