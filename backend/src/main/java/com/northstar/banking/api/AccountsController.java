package com.northstar.banking.api;

import com.northstar.banking.core.MockCoreBankingService;
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
    private final MockCoreBankingService core;

    public AccountsController(MockCoreBankingService core) {
        this.core = core;
    }

    @GetMapping({"/api/core/accounts", "/api/modules/casa"})
    public List<Account> accounts() {
        return core.dashboard().accounts();
    }

    @PostMapping("/api/modules/casa")
    public Account openCasa(@RequestBody CasaRequest request) {
        return core.openCasa(request);
    }

    @GetMapping("/api/modules/casa/{accountId}")
    public Account accountDetails(@PathVariable String accountId) {
        return core.accountDetails(accountId);
    }

    @GetMapping("/api/modules/casa/{accountId}/statement")
    public AccountStatement accountStatement(@PathVariable String accountId) {
        return core.accountStatement(accountId);
    }
}
