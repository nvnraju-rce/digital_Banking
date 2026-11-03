package com.northstar.banking.application;

import com.northstar.banking.model.BankingModels.Account;
import com.northstar.banking.model.BankingModels.AccountStatement;
import com.northstar.banking.model.ProductModels.CasaRequest;
import com.northstar.banking.port.out.CoreBankingPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    private final CoreBankingPort core;

    public AccountService(CoreBankingPort core) { this.core = core; }

    public List<Account> accounts() { return core.dashboard().accounts(); }
    public Account openAccount(CasaRequest request) { return core.openAccount(request); }
    public Account details(String accountId) { return core.accountDetails(accountId); }
    public AccountStatement statement(String accountId) { return core.accountStatement(accountId); }
}
