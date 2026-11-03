package com.northstar.banking.api;

import com.northstar.banking.core.MockCoreBankingService;
import com.northstar.banking.model.BankingModels.DashboardResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {
    private final MockCoreBankingService core;

    public DashboardController(MockCoreBankingService core) {
        this.core = core;
    }

    @GetMapping("/api/core/dashboard")
    public DashboardResponse dashboard() {
        return core.dashboard();
    }
}
