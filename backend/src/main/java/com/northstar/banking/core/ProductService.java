package com.northstar.banking.core;

import com.northstar.banking.model.ProductModels.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProductService {
    private static final BigDecimal DEPOSIT_RATE = new BigDecimal("6.75");
    private static final BigDecimal LOAN_RATE = new BigDecimal("12.00");
    private final Map<String, List<FixedDeposit>> deposits = new HashMap<>();
    private final Map<String, List<Loan>> loans = new HashMap<>();
    private final AtomicLong sequence = new AtomicLong(1003);
    private final MockCoreBankingService core;

    public ProductService(MockCoreBankingService core) {
        this.core = core;
        seedDemoProducts();
    }

    public synchronized List<FixedDeposit> deposits(String user) {
        return List.copyOf(deposits.getOrDefault(user, List.of()));
    }

    public synchronized FixedDeposit depositDetails(String user, String depositId) {
        return findDeposit(user, depositId);
    }

    public synchronized DepositStatement depositStatement(String user, String depositId) {
        FixedDeposit deposit = findDeposit(user, depositId);
        LocalDate openedOn = deposit.maturityDate().minusMonths(deposit.tenureMonths());
        List<DepositStatementLine> entries = List.of(
                new DepositStatementLine(openedOn, "Principal placed", deposit.principal().negate(), deposit.principal()),
                new DepositStatementLine(deposit.maturityDate(), "Projected maturity value", deposit.maturityValue(), deposit.maturityValue())
        );
        return new DepositStatement(deposit, entries);
    }

    public synchronized FixedDeposit openDeposit(String user, DepositRequest request) {
        if (request == null || request.sourceAccountId() == null || request.principal() == null
                || request.principal().signum() <= 0 || request.tenureMonths() < 1 || request.tenureMonths() > 120) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Provide an account, positive principal and tenure from 1 to 120 months.");
        }
        core.reserveForDeposit(request.sourceAccountId(), request.principal());
        BigDecimal maturity = request.principal()
                .multiply(BigDecimal.ONE.add(DEPOSIT_RATE.divide(new BigDecimal("1200"), 8, RoundingMode.HALF_UP))
                        .pow(request.tenureMonths()))
                .setScale(2, RoundingMode.HALF_UP);
        FixedDeposit deposit = new FixedDeposit("FD-" + sequence.getAndIncrement(), request.sourceAccountId(),
                request.principal().setScale(2, RoundingMode.HALF_UP), DEPOSIT_RATE, request.tenureMonths(),
                maturity, LocalDate.now().plusMonths(request.tenureMonths()), "ACTIVE");
        deposits.computeIfAbsent(user, ignored -> new ArrayList<>()).add(deposit);
        return deposit;
    }

    public synchronized List<Loan> loans(String user) {
        return List.copyOf(loans.getOrDefault(user, List.of()));
    }

    public synchronized Loan loanDetails(String user, String loanId) {
        return findLoan(user, loanId);
    }

    public synchronized LoanStatement loanStatement(String user, String loanId) {
        Loan loan = findLoan(user, loanId);
        BigDecimal monthlyRate = LOAN_RATE.divide(new BigDecimal("1200"), 10, RoundingMode.HALF_UP);
        BigDecimal remaining = loan.amount().setScale(2, RoundingMode.HALF_UP);
        List<LoanInstallment> installments = new ArrayList<>();
        for (int number = 1; number <= loan.tenureMonths() && remaining.signum() > 0; number++) {
            BigDecimal interest = remaining.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principal = loan.indicativeMonthlyPayment().subtract(interest)
                    .max(BigDecimal.ZERO).min(remaining).setScale(2, RoundingMode.HALF_UP);
            BigDecimal payment = principal.add(interest).setScale(2, RoundingMode.HALF_UP);
            remaining = remaining.subtract(principal).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
            installments.add(new LoanInstallment(number, loan.appliedOn().plusMonths(number), payment,
                    principal, interest, remaining));
        }
        return new LoanStatement(loan, LOAN_RATE, List.copyOf(installments));
    }

    public synchronized Loan applyLoan(String user, LoanRequest request) {
        if (request == null || request.amount() == null || request.amount().signum() <= 0
                || request.tenureMonths() < 1 || request.tenureMonths() > 360
                || request.purpose() == null || request.purpose().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Enter a valid amount, purpose and tenure from 1 to 360 months.");
        }
        BigDecimal monthlyRate = LOAN_RATE.divide(new BigDecimal("1200"), 10, RoundingMode.HALF_UP);
        double power = Math.pow(1 + monthlyRate.doubleValue(), request.tenureMonths());
        BigDecimal emi = request.amount().multiply(monthlyRate).multiply(BigDecimal.valueOf(power))
                .divide(BigDecimal.valueOf(power - 1), 2, RoundingMode.HALF_UP);
        Loan loan = new Loan("LN-" + sequence.getAndIncrement(), request.amount().setScale(2, RoundingMode.HALF_UP),
                request.tenureMonths(), request.purpose().trim(), emi, "UNDER_REVIEW", LocalDate.now());
        loans.computeIfAbsent(user, ignored -> new ArrayList<>()).add(loan);
        return loan;
    }

    private FixedDeposit findDeposit(String user, String depositId) {
        return deposits.getOrDefault(user, List.of()).stream()
                .filter(deposit -> deposit.id().equals(depositId)).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fixed deposit was not found."));
    }

    private Loan findLoan(String user, String loanId) {
        return loans.getOrDefault(user, List.of()).stream()
                .filter(loan -> loan.id().equals(loanId)).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan application was not found."));
    }

    private void seedDemoProducts() {
        LocalDate today = LocalDate.now();
        FixedDeposit travelFund = seededDeposit("FD-1001", "ACC-8831", new BigDecimal("50000.00"), 12, today);
        FixedDeposit reserveFund = seededDeposit("FD-1002", "ACC-4620", new BigDecimal("25000.00"), 24, today);
        core.reserveForDeposit(travelFund.sourceAccountId(), travelFund.principal());
        core.reserveForDeposit(reserveFund.sourceAccountId(), reserveFund.principal());
        deposits.put("demo", new ArrayList<>(List.of(travelFund, reserveFund)));

        loans.put("demo", new ArrayList<>(List.of(
                seededLoan("LN-2001", new BigDecimal("500000.00"), 36, "Home improvement", today),
                seededLoan("LN-2002", new BigDecimal("150000.00"), 24, "Education", today)
        )));
    }

    private FixedDeposit seededDeposit(String id, String accountId, BigDecimal principal, int months, LocalDate openedOn) {
        BigDecimal maturity = principal
                .multiply(BigDecimal.ONE.add(DEPOSIT_RATE.divide(new BigDecimal("1200"), 8, RoundingMode.HALF_UP)).pow(months))
                .setScale(2, RoundingMode.HALF_UP);
        return new FixedDeposit(id, accountId, principal, DEPOSIT_RATE, months, maturity,
                openedOn.plusMonths(months), "ACTIVE");
    }

    private Loan seededLoan(String id, BigDecimal amount, int months, String purpose, LocalDate appliedOn) {
        BigDecimal monthlyRate = LOAN_RATE.divide(new BigDecimal("1200"), 10, RoundingMode.HALF_UP);
        double power = Math.pow(1 + monthlyRate.doubleValue(), months);
        BigDecimal emi = amount.multiply(monthlyRate).multiply(BigDecimal.valueOf(power))
                .divide(BigDecimal.valueOf(power - 1), 2, RoundingMode.HALF_UP);
        return new Loan(id, amount, months, purpose, emi, "UNDER_REVIEW", appliedOn);
    }
}
