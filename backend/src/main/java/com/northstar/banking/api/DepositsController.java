package com.northstar.banking.api;

import com.northstar.banking.core.ProductService;
import com.northstar.banking.model.ProductModels.DepositRequest;
import com.northstar.banking.model.ProductModels.FixedDeposit;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DepositsController {
    private final ProductService products;

    public DepositsController(ProductService products) {
        this.products = products;
    }

    @GetMapping("/api/modules/deposits")
    public List<FixedDeposit> deposits(@AuthenticationPrincipal Jwt jwt) {
        return products.deposits(jwt.getSubject());
    }

    @PostMapping("/api/modules/deposits")
    public FixedDeposit openDeposit(@AuthenticationPrincipal Jwt jwt, @RequestBody DepositRequest request) {
        return products.openDeposit(jwt.getSubject(), request);
    }
}
