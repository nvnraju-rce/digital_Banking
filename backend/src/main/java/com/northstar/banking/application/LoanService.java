package com.northstar.banking.application;

import com.northstar.banking.model.ProductModels.Loan;
import com.northstar.banking.model.ProductModels.LoanInstallment;
import com.northstar.banking.model.ProductModels.LoanRequest;
import com.northstar.banking.model.ProductModels.LoanStatement;
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
public class LoanService {
    private static final String DEMO_USER = "demo";
    private static final BigDecimal ANNUAL_RATE = new BigDecimal("12.00");
    private final ProductDataPort products;
    private final AtomicLong sequence = new AtomicLong(1003);

    public LoanService(ProductDataPort products) {
        this.products = products;
        seedDemoLoans();
    }

    public List<Loan> loans(String userId) { return products.loans(userId); }

    public Loan details(String userId, String loanId) { return findLoan(userId, loanId); }

    public LoanStatement statement(String userId, String loanId) {
        Loan loan = findLoan(userId, loanId);
        BigDecimal monthlyRate = monthlyRate();
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
        return new LoanStatement(loan, ANNUAL_RATE, List.copyOf(installments));
    }

    public synchronized Loan apply(String userId, LoanRequest request) {
        if (request == null || request.amount() == null || request.amount().signum() <= 0
                || request.tenureMonths() < 1 || request.tenureMonths() > 360
                || request.purpose() == null || request.purpose().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Enter a valid amount, purpose and tenure from 1 to 360 months.");
        }
        Loan loan = createLoan("LN-" + sequence.getAndIncrement(), request.amount(), request.tenureMonths(),
                request.purpose().trim(), LocalDate.now());
        products.saveLoan(userId, loan);
        return loan;
    }

    private Loan findLoan(String userId, String loanId) {
        return products.findLoan(userId, loanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan application was not found."));
    }

    private void seedDemoLoans() {
        LocalDate today = LocalDate.now();
        products.saveLoan(DEMO_USER, createLoan("LN-2001", new BigDecimal("500000.00"), 36, "Home improvement", today));
        products.saveLoan(DEMO_USER, createLoan("LN-2002", new BigDecimal("150000.00"), 24, "Education", today));
    }

    private Loan createLoan(String id, BigDecimal amount, int months, String purpose, LocalDate appliedOn) {
        BigDecimal roundedAmount = amount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal emi = monthlyPayment(roundedAmount, months);
        return new Loan(id, roundedAmount, months, purpose, emi, "UNDER_REVIEW", appliedOn);
    }

    private BigDecimal monthlyPayment(BigDecimal amount, int months) {
        BigDecimal rate = monthlyRate();
        double power = Math.pow(1 + rate.doubleValue(), months);
        return amount.multiply(rate).multiply(BigDecimal.valueOf(power))
                .divide(BigDecimal.valueOf(power - 1), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal monthlyRate() {
        return ANNUAL_RATE.divide(new BigDecimal("1200"), 10, RoundingMode.HALF_UP);
    }
}
