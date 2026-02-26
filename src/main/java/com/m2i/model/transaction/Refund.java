package com.m2i.model.transaction;

import com.m2i.model.account.Account;

import java.math.BigDecimal;

public sealed abstract class Refund extends Operation permits RefundCredit , RefundDebit, RefundTransfer {

    public Refund (Account account, BigDecimal amount, String description) {
        super(account, amount, TransactionType.REFUND, description);
    }
}
