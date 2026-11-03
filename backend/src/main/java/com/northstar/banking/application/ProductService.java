package com.northstar.banking.application;

import com.northstar.banking.model.ProductModels.*;
import com.northstar.banking.payment.port.out.AccountLedgerPort;
import com.northstar.banking.port.out.ProductDataPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProductService {
    private static final String DEMO_USER = "demo";
    private static final BigDecimal DEPOSIT_RATE = new BigDecimal("6.75");
    private static final BigDecimal LOAN_RATE = new BigDecimal("12.00");
    private final AtomicLong sequence = new AtomicLong(1003);
    private final AccountLedgerPort ledger;
    private final ProductDataPort productData;

    public ProductService(AccountLedgerPort ledger, ProductDataPort productData) {
        this.ledger = ledger;
        this.productData = productData;
        seedDemoProducts();
    }

    public List<FixedDeposit> deposits(String userId) {
        return productData.deposits(userId);
    }

    public FixedDeposit depositDetails(String userId, String depositId) {
        return findDeposit(userId, depositId);
    }

    public DepositStatement depositStatement(String userId, String depositId) {
        FixedDeposit deposit = findDeposit(userId, depositId);
        LocalDate openedOn = deposit.maturityDate().minusMonths(deposit.tenureMonths());
        List<DepositStatementLine> entries = List.of(
                new DepositStatementLine(openedOn, "Principal placed", deposit.principal().negate(), deposit.principal()),
                new DepositStatementLine(deposit.maturityDate(), "Projected maturity value", deposit.maturityValue(), deposit.maturityValue())
        );
        return new DepositStatement(deposit, entries);
    }

    public synchronized FixedDeposit openDeposit(String userId, DepositRequest request) {
        if (request == null || request.sourceAccountId() == null || request.principal() == null
                || request.principal().signum() <= 0 || request.tenureMonths() < 1 || request.tenureMonths() > 120) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Provide an account, positive principal and tenure from 1 to 120 months.");
        }
        ledger.reserveForDeposit(request.sourceAccountId(), request.principal());
        BigDecimal maturity = maturityValue(request.principal(), request.tenureMonths());
        FixedDeposit deposit = new FixedDeposit("FD-" + sequence.getAndIncrement(), request.sourceAccountId(),
                request.principal().setScale(2, RoundingMode.HALF_UP), DEPOSIT_RATE, request.tenureMonths(),
                maturity, LocalDate.now().plusMonths(request.tenureMonths()), "ACTIVE");
        productData.saveDeposit(userId, deposit);
        return deposit;
    }

    public List<Loan> loans(String userId) {
        return productData.loans(userId);
    }

    public Loan loanDetails(String userId, String loanId) {
        return findLoan(userId, loanId);
    }

    public LoanStatement loanStatement(String userId, String loanId) {
        Loan loan = findLoan(userId, loanId);
        BigDecimal monthlyRate = monthlyLoanRate();
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

    public synchronized Loan applyLoan(String userId, LoanRequest request) {
        if (request == null || request.amount() == null || request.amount().signum() <= 0
                || request.tenureMonths() < 1 || request.tenureMonths() > 360
                || request.purpose() == null || request.purpose().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Enter a valid amount, purpose and tenure from 1 to 360 months.");
        }
        Loan loan = new Loan("LN-" + sequence.getAndIncrement(),
                request.amount().setScale(2, RoundingMode.HALF_UP), request.tenureMonths(),
                request.purpose().trim(), monthlyPayment(request.amount(), request.tenureMonths()),
                "UNDER_REVIEW", LocalDate.now());
        productData.saveLoan(userId, loan);
        return loan;
    }

    private FixedDeposit findDeposit(String userId, String depositId) {
        return productData.findDeposit(userId, depositId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fixed deposit was not found."));
    }

    private Loan findLoan(String userId, String loanId) {
        return productData.findLoan(userId, loanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan application was not found."));
    }

    private void seedDemoProducts() {
        LocalDate today = LocalDate.now();
        FixedDeposit travelFund = seededDeposit("FD-1001", "ACC-8831", new BigDecimal("50000.00"), 12, today);
        FixedDeposit reserveFund = seededDeposit("FD-1002", "ACC-4620", new BigDecimal("25000.00"), 24, today);
        ledger.reserveForDeposit(travelFund.sourceAccountId(), travelFund.principal());
        ledger.reserveForDeposit(reserveFund.sourceAccountId(), reserveFund.principal());
        productData.saveDeposit(DEMO_USER, travelFund);
        productData.saveDeposit(DEMO_USER, reserveFund);
        productData.saveLoan(DEMO_USER,
                seededLoan("LN-2001", new BigDecimal("500000.00"), 36, "Home improvement", today));
        productData.saveLoan(DEMO_USER,
                seededLoan("LN-2002", new BigDecimal("150000.00"), 24, "Education", today));
    }

    private FixedDeposit seededDeposit(String id, String accountId, BigDecimal principal, int months, LocalDate openedOn) {
        return new FixedDeposit(id, accountId, principal, DEPOSIT_RATE, months,
                maturityValue(principal, months), openedOn.plusMonths(months), "ACTIVE");
    }

    private BigDecimal maturityValue(BigDecimal principal, int months) {
        return principal.multiply(BigDecimal.ONE.add(
                        DEPOSIT_RATE.divide(new BigDecimal("1200"), 8, RoundingMode.HALF_UP)).pow(months))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private Loan seededLoan(String id, BigDecimal amount, int months, String purpose, LocalDate appliedOn) {
        return new Loan(id, amount, months, purpose, monthlyPayment(amount, months), "UNDER_REVIEW", appliedOn);
    }

    private BigDecimal monthlyPayment(BigDecimal amount, int months) {
        BigDecimal monthlyRate = monthlyLoanRate();
        double power = Math.pow(1 + monthlyRate.doubleValue(), months);
        return amount.multiply(monthlyRate).multiply(BigDecimal.valueOf(power))
                .divide(BigDecimal.valueOf(power - 1), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal monthlyLoanRate() {
        return LOAN_RATE.divide(new BigDecimal("1200"), 10, RoundingMode.HALF_UP);
    }
}
