package com.northstar.banking.api;

import com.northstar.banking.application.AccountService;
import com.northstar.banking.model.BankingModels.Account;
import com.northstar.banking.model.BankingModels.AccountStatement;
import com.northstar.banking.model.ProductModels.CasaRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AccountsController {
    private final AccountService accounts;

    public AccountsController(AccountService accounts) {
        this.accounts = accounts;
    }

    @GetMapping({"/api/core/accounts", "/api/modules/casa"})
    public List<Account> accounts() {
        return accounts.accounts();
    }

    @PostMapping("/api/modules/casa")
    public Account openCasa(@RequestBody CasaRequest request) {
        return accounts.openAccount(request);
    }

    @GetMapping("/api/modules/casa/{accountId}")
    public Account accountDetails(@PathVariable String accountId) {
        return accounts.details(accountId);
    }

    @GetMapping("/api/modules/casa/{accountId}/statement")
    public AccountStatement accountStatement(@PathVariable String accountId) {
        return accounts.statement(accountId);
    }
}
