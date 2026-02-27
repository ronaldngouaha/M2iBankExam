package com.m2i.model.transaction;

import com.m2i.model.account.Account;
import java.math.BigDecimal;

public non-sealed class RefundTransfer  <T extends Account> extends Refund {

    private final Account receiver;

    public RefundTransfer(T sender, T receiver, BigDecimal amount, String description) {
        super(sender, amount, description);
        this.receiver = receiver;
    }

    public Account getReceiver() {
        return receiver;
    }
}
