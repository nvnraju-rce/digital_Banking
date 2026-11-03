package com.northstar.banking.application;

import com.northstar.banking.model.ProductModels.DepositRequest;
import com.northstar.banking.model.ProductModels.DepositStatement;
import com.northstar.banking.model.ProductModels.DepositStatementLine;
import com.northstar.banking.model.ProductModels.FixedDeposit;
import com.northstar.banking.payment.port.out.AccountLedgerPort;
import com.northstar.banking.port.out.ProductDataPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class DepositService {
    private static final String DEMO_USER = "demo";
    private static final BigDecimal ANNUAL_RATE = new BigDecimal("6.75");
    private final ProductDataPort products;
    private final AccountLedgerPort ledger;
    private final AtomicLong sequence = new AtomicLong(1003);

    public DepositService(ProductDataPort products, AccountLedgerPort ledger) {
        this.products = products;
        this.ledger = ledger;
        seedDemoDeposits();
    }

    public List<FixedDeposit> deposits(String userId) { return products.deposits(userId); }

    public FixedDeposit details(String userId, String depositId) { return findDeposit(userId, depositId); }

    public DepositStatement statement(String userId, String depositId) {
        FixedDeposit deposit = findDeposit(userId, depositId);
        LocalDate openedOn = deposit.maturityDate().minusMonths(deposit.tenureMonths());
        return new DepositStatement(deposit, List.of(
                new DepositStatementLine(openedOn, "Principal placed", deposit.principal().negate(), deposit.principal()),
                new DepositStatementLine(deposit.maturityDate(), "Projected maturity value", deposit.maturityValue(), deposit.maturityValue())
        ));
    }

    public synchronized FixedDeposit open(String userId, DepositRequest request) {
        if (request == null || request.sourceAccountId() == null || request.principal() == null
                || request.principal().signum() <= 0 || request.tenureMonths() < 1 || request.tenureMonths() > 120) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Provide an account, positive principal and tenure from 1 to 120 months.");
        }
        ledger.reserveForDeposit(request.sourceAccountId(), request.principal());
        FixedDeposit deposit = createDeposit("FD-" + sequence.getAndIncrement(), request.sourceAccountId(),
                request.principal(), request.tenureMonths(), LocalDate.now());
        products.saveDeposit(userId, deposit);
        return deposit;
    }

    private FixedDeposit findDeposit(String userId, String depositId) {
        return products.findDeposit(userId, depositId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fixed deposit was not found."));
    }

    private void seedDemoDeposits() {
        LocalDate today = LocalDate.now();
        FixedDeposit travelFund = createDeposit("FD-1001", "ACC-8831", new BigDecimal("50000.00"), 12, today);
        FixedDeposit reserveFund = createDeposit("FD-1002", "ACC-4620", new BigDecimal("25000.00"), 24, today);
        ledger.reserveForDeposit(travelFund.sourceAccountId(), travelFund.principal());
        ledger.reserveForDeposit(reserveFund.sourceAccountId(), reserveFund.principal());
        products.saveDeposit(DEMO_USER, travelFund);
        products.saveDeposit(DEMO_USER, reserveFund);
    }

    private FixedDeposit createDeposit(String id, String accountId, BigDecimal principal, int months, LocalDate openedOn) {
        BigDecimal maturity = principal.multiply(BigDecimal.ONE.add(
                        ANNUAL_RATE.divide(new BigDecimal("1200"), 8, RoundingMode.HALF_UP)).pow(months))
                .setScale(2, RoundingMode.HALF_UP);
        return new FixedDeposit(id, accountId, principal.setScale(2, RoundingMode.HALF_UP), ANNUAL_RATE,
                months, maturity, openedOn.plusMonths(months), "ACTIVE");
    }
}
