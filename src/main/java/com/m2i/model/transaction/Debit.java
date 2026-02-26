package com.m2i.model.transaction;

import com.m2i.model.account.Account;

import java.math.BigDecimal;

public final class Debit extends Operation {

    public Debit (Account client, BigDecimal amount, String description) {
        super(client, amount, TransactionType.DEBIT, description);
    }

}
