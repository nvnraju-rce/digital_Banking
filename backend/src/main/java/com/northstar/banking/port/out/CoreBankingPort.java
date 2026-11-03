package com.northstar.banking.port.out;

import com.northstar.banking.model.BankingModels.Account;
import com.northstar.banking.model.BankingModels.AccountStatement;
import com.northstar.banking.model.BankingModels.BankCard;
import com.northstar.banking.model.BankingModels.DashboardResponse;
import com.northstar.banking.model.BankingModels.TransferRequest;
import com.northstar.banking.model.BankingModels.TransferResponse;
import com.northstar.banking.model.ProductModels.CasaRequest;

public interface CoreBankingPort {
    DashboardResponse dashboard();
    Account openAccount(CasaRequest request);
    Account accountDetails(String accountId);
    AccountStatement accountStatement(String accountId);
    TransferResponse transfer(TransferRequest request);
    BankCard setCardFrozen(String cardId, boolean frozen);
}
