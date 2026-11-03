package com.northstar.banking.application;

import com.northstar.banking.model.BankingModels.BankCard;
import com.northstar.banking.port.out.CoreBankingPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {
    private final CoreBankingPort core;

    public CardService(CoreBankingPort core) { this.core = core; }

    public List<BankCard> cards() { return core.dashboard().cards(); }
    public BankCard setFrozen(String cardId, boolean frozen) { return core.setCardFrozen(cardId, frozen); }
}
