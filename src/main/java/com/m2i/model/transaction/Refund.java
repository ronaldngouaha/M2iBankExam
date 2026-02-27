package com.m2i.model.transaction;

import com.m2i.model.account.Account;

import java.math.BigDecimal;

public sealed abstract class Refund  <T extends Account> extends FinancialOperation permits RefundCredit , RefundDebit, RefundTransfer {

    public Refund (Account account, BigDecimal amount, String description) {
        super(account, amount, OperationType.REFUND, description);
    }
}
