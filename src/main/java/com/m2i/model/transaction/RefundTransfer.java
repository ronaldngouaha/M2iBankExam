package com.m2i.model.transaction;

import com.m2i.model.account.Account;
import java.math.BigDecimal;

public final class RefundTransfer extends Refund {

    private final Account receiver;

    public RefundTransfer(Account sender, Account receiver, BigDecimal amount, String description) {
        super(sender, amount, description);
        this.receiver = receiver;
    }

    public Account getReceiver() {
        return receiver;
    }
}
