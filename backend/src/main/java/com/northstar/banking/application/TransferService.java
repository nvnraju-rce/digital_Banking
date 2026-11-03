package com.northstar.banking.application;

import com.northstar.banking.model.BankingModels.TransferRequest;
import com.northstar.banking.model.BankingModels.TransferResponse;
import com.northstar.banking.port.out.CoreBankingPort;
import org.springframework.stereotype.Service;

@Service
public class TransferService {
    private final CoreBankingPort core;

    public TransferService(CoreBankingPort core) { this.core = core; }

    public TransferResponse transfer(TransferRequest request) { return core.transfer(request); }
}
