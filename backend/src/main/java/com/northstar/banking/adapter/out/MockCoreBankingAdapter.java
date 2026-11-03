package com.northstar.banking.adapter.out;

import com.northstar.banking.core.MockCoreBankingService;
import com.northstar.banking.model.BankingModels.Account;
import com.northstar.banking.model.BankingModels.AccountStatement;
import com.northstar.banking.model.BankingModels.BankCard;
import com.northstar.banking.model.BankingModels.DashboardResponse;
import com.northstar.banking.model.BankingModels.TransferRequest;
import com.northstar.banking.model.BankingModels.TransferResponse;
import com.northstar.banking.model.ProductModels.CasaRequest;
import com.northstar.banking.payment.port.out.AccountLedgerPort;
import com.northstar.banking.port.out.CoreBankingPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MockCoreBankingAdapter implements CoreBankingPort, AccountLedgerPort {
    private final MockCoreBankingService mockCore;

    public MockCoreBankingAdapter(MockCoreBankingService mockCore) {
        this.mockCore = mockCore;
    }

    @Override
    public DashboardResponse dashboard() {
        return mockCore.dashboard();
    }

    @Override
    public Account openAccount(CasaRequest request) {
        return mockCore.openCasa(request);
    }

    @Override
    public Account accountDetails(String accountId) {
        return mockCore.accountDetails(accountId);
    }

    @Override
    public AccountStatement accountStatement(String accountId) {
        return mockCore.accountStatement(accountId);
    }

    @Override
    public TransferResponse transfer(TransferRequest request) {
        return mockCore.transfer(request);
    }

    @Override
    public BankCard setCardFrozen(String cardId, boolean frozen) {
        return mockCore.setCardFrozen(cardId, frozen);
    }

    @Override
    public TransferResponse transferOwnAccounts(TransferRequest request) {
        return mockCore.transfer(request);
    }

    @Override
    public void debitForPayment(String sourceAccountId, BigDecimal paymentAmount, BigDecimal fee,
                                String category, String memo) {
        mockCore.debitForPayment(sourceAccountId, paymentAmount, fee, category, memo);
    }

    @Override
    public void reserveForDeposit(String sourceAccountId, BigDecimal principal) {
        mockCore.reserveForDeposit(sourceAccountId, principal);
    }
}
