package com.northstar.banking.adapter.out;

import com.northstar.banking.model.ProductModels.FixedDeposit;
import com.northstar.banking.model.ProductModels.Loan;
import com.northstar.banking.port.out.ProductDataPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class MockProductDataAdapter implements ProductDataPort {
    private final Map<String, List<FixedDeposit>> deposits = new HashMap<>();
    private final Map<String, List<Loan>> loans = new HashMap<>();

    @Override
    public synchronized List<FixedDeposit> deposits(String userId) {
        return List.copyOf(deposits.getOrDefault(userId, List.of()));
    }

    @Override
    public synchronized Optional<FixedDeposit> findDeposit(String userId, String depositId) {
        return deposits.getOrDefault(userId, List.of()).stream()
                .filter(deposit -> deposit.id().equals(depositId)).findFirst();
    }

    @Override
    public synchronized void saveDeposit(String userId, FixedDeposit deposit) {
        deposits.computeIfAbsent(userId, ignored -> new ArrayList<>()).add(deposit);
    }

    @Override
    public synchronized List<Loan> loans(String userId) {
        return List.copyOf(loans.getOrDefault(userId, List.of()));
    }

    @Override
    public synchronized Optional<Loan> findLoan(String userId, String loanId) {
        return loans.getOrDefault(userId, List.of()).stream()
                .filter(loan -> loan.id().equals(loanId)).findFirst();
    }

    @Override
    public synchronized void saveLoan(String userId, Loan loan) {
        loans.computeIfAbsent(userId, ignored -> new ArrayList<>()).add(loan);
    }
}
