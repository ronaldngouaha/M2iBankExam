package com.m2i.model.transaction;


import com.m2i.model.account.Account;
import java.math.BigDecimal;

public non-sealed class RefundDebit  <T extends Account> extends Refund {

    public RefundDebit(T account, BigDecimal amount, String description) {
        super(account, amount, description);
    }
}
