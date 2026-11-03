package com.northstar.banking.api;

import com.northstar.banking.application.CardService;
import com.northstar.banking.model.BankingModels.BankCard;
import com.northstar.banking.model.BankingModels.FreezeRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class CardsController {
    private final CardService cardsService;

    public CardsController(CardService cardsService) {
        this.cardsService = cardsService;
    }

    @GetMapping("/api/core/cards")
    public List<BankCard> cards() {
        return cardsService.cards();
    }

    @PatchMapping("/api/core/cards/{cardId}")
    public BankCard freezeCard(@PathVariable String cardId, @RequestBody(required = false) FreezeRequest request) {
        if (request == null || request.frozen() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide the frozen state.");
        }
        return cardsService.setFrozen(cardId, request.frozen());
    }
}
