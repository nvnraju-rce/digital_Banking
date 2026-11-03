package com.northstar.banking.port.out;

import com.northstar.banking.model.ProductModels.FixedDeposit;
import com.northstar.banking.model.ProductModels.Loan;

import java.util.List;
import java.util.Optional;

public interface ProductDataPort {
    List<FixedDeposit> deposits(String userId);
    Optional<FixedDeposit> findDeposit(String userId, String depositId);
    void saveDeposit(String userId, FixedDeposit deposit);
    List<Loan> loans(String userId);
    Optional<Loan> findLoan(String userId, String loanId);
    void saveLoan(String userId, Loan loan);
}
