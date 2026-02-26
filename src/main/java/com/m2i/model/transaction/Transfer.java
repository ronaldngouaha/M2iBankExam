package com.m2i.model.transaction;
import com.m2i.model.account.Account;
import java.math.BigDecimal;

public final class Transfer extends Operation {

    private Account receiver;

    public Transfer(Account sender, Account receiver, BigDecimal amount, String description) {
        super(sender, amount, TransactionType.TRANSFER, description);
        this.receiver = receiver;

    }

    public Account getReceiver() {
        return receiver;
    }
}

