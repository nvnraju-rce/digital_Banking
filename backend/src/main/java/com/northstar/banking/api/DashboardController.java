package com.northstar.banking.api;

import com.northstar.banking.application.DashboardService;
import com.northstar.banking.model.BankingModels.DashboardResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {
    private final DashboardService dashboard;

    public DashboardController(DashboardService dashboard) {
        this.dashboard = dashboard;
    }

    @GetMapping("/api/core/dashboard")
    public DashboardResponse dashboard() {
        return dashboard.dashboard();
    }
}
