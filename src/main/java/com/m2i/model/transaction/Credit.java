package com.m2i.model.transaction;

import com.m2i.model.account.Account;

import java.math.BigDecimal;

public final class Credit extends Operation {

    public Credit (Account client, BigDecimal amount, String description) {
        super(client, amount, TransactionType.CREDIT, description);
    }

}
