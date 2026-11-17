package com.northstar.banking.adapter.out;

import com.northstar.banking.core.CoreBankingService;
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
public class CoreBankingAdapter implements CoreBankingPort, AccountLedgerPort {
    private final CoreBankingService coreLedger;

    public CoreBankingAdapter(CoreBankingService coreLedger) {
        this.coreLedger = coreLedger;
    }

    @Override
    public DashboardResponse dashboard() {
        return coreLedger.dashboard();
    }

    @Override
    public Account openAccount(CasaRequest request) {
        return coreLedger.openCasa(request);
    }

    @Override
    public Account accountDetails(String accountId) {
        return coreLedger.accountDetails(accountId);
    }

    @Override
    public AccountStatement accountStatement(String accountId) {
        return coreLedger.accountStatement(accountId);
    }

    @Override
    public TransferResponse transfer(TransferRequest request) {
        return coreLedger.transfer(request);
    }

    @Override
    public BankCard setCardFrozen(String cardId, boolean frozen) {
        return coreLedger.setCardFrozen(cardId, frozen);
    }

    @Override
    public TransferResponse transferOwnAccounts(TransferRequest request) {
        return coreLedger.transfer(request);
    }

    @Override
    public void debitForPayment(String sourceAccountId, BigDecimal paymentAmount, BigDecimal fee,
                                String category, String memo) {
        coreLedger.debitForPayment(sourceAccountId, paymentAmount, fee, category, memo);
    }

    @Override
    public void reserveForDeposit(String sourceAccountId, BigDecimal principal) {
        coreLedger.reserveForDeposit(sourceAccountId, principal);
    }
}
