package com.northstar.banking.application;

import com.northstar.banking.model.BankingModels.Account;
import com.northstar.banking.model.BankingModels.AccountStatement;
import com.northstar.banking.model.BankingModels.BankCard;
import com.northstar.banking.model.BankingModels.DashboardResponse;
import com.northstar.banking.model.BankingModels.Transaction;
import com.northstar.banking.model.BankingModels.TransferRequest;
import com.northstar.banking.model.BankingModels.TransferResponse;
import com.northstar.banking.model.ProductModels.CasaRequest;
import com.northstar.banking.port.out.CoreBankingPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoreBankingService {
    private final CoreBankingPort coreBanking;

    public CoreBankingService(CoreBankingPort coreBanking) {
        this.coreBanking = coreBanking;
    }

    public DashboardResponse dashboard() {
        return coreBanking.dashboard();
    }

    public List<Account> accounts() {
        return dashboard().accounts();
    }

    public List<Transaction> transactions() {
        return dashboard().transactions();
    }

    public List<BankCard> cards() {
        return dashboard().cards();
    }

    public Account openAccount(CasaRequest request) {
        return coreBanking.openAccount(request);
    }

    public Account accountDetails(String accountId) {
        return coreBanking.accountDetails(accountId);
    }

    public AccountStatement accountStatement(String accountId) {
        return coreBanking.accountStatement(accountId);
    }

    public TransferResponse transfer(TransferRequest request) {
        return coreBanking.transfer(request);
    }

    public BankCard setCardFrozen(String cardId, boolean frozen) {
        return coreBanking.setCardFrozen(cardId, frozen);
    }
}
