package com.northstar.banking.api;

import com.northstar.banking.application.CoreBankingService;
import com.northstar.banking.model.BankingModels.DashboardResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {
    private final CoreBankingService core;

    public DashboardController(CoreBankingService core) {
        this.core = core;
    }

    @GetMapping("/api/core/dashboard")
    public DashboardResponse dashboard() {
        return core.dashboard();
    }
}
