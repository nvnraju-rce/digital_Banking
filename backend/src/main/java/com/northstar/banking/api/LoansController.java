package com.northstar.banking.api;

import com.northstar.banking.application.ProductService;
import com.northstar.banking.model.ProductModels.Loan;
import com.northstar.banking.model.ProductModels.LoanRequest;
import com.northstar.banking.model.ProductModels.LoanStatement;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoansController {
    private final ProductService products;

    public LoansController(ProductService products) {
        this.products = products;
    }

    @GetMapping("/api/modules/loans")
    public List<Loan> loans(@AuthenticationPrincipal Jwt jwt) {
        return products.loans(jwt.getSubject());
    }

    @PostMapping("/api/modules/loans")
    public Loan applyLoan(@AuthenticationPrincipal Jwt jwt, @RequestBody LoanRequest request) {
        return products.applyLoan(jwt.getSubject(), request);
    }

    @GetMapping("/api/modules/loans/{loanId}")
    public Loan loanDetails(@AuthenticationPrincipal Jwt jwt, @PathVariable String loanId) {
        return products.loanDetails(jwt.getSubject(), loanId);
    }

    @GetMapping("/api/modules/loans/{loanId}/statement")
    public LoanStatement loanStatement(@AuthenticationPrincipal Jwt jwt, @PathVariable String loanId) {
        return products.loanStatement(jwt.getSubject(), loanId);
    }
}
