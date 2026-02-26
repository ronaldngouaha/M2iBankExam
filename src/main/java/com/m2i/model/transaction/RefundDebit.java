package com.m2i.model.transaction;


import com.m2i.model.account.Account;
import java.math.BigDecimal;

public final class RefundDebit extends Refund {

    public RefundDebit(Account account, BigDecimal amount, String description) {
        super(account, amount, description);
    }
}
