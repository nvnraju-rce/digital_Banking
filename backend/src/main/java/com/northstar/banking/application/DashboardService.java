package com.northstar.banking.application;

import com.northstar.banking.model.BankingModels.DashboardResponse;
import com.northstar.banking.port.out.CoreBankingPort;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private final CoreBankingPort core;

    public DashboardService(CoreBankingPort core) { this.core = core; }

    public DashboardResponse dashboard() { return core.dashboard(); }
}
