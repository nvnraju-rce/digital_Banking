package com.northstar.banking.api;

import com.northstar.banking.application.CoreBankingService;
import com.northstar.banking.model.BankingModels.TransferRequest;
import com.northstar.banking.model.BankingModels.TransferResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransfersController {
    private final CoreBankingService core;

    public TransfersController(CoreBankingService core) {
        this.core = core;
    }

    @PostMapping("/api/core/transfers")
    public TransferResponse transfer(@RequestBody TransferRequest request) {
        return core.transfer(request);
    }
}
