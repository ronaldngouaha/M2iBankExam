package com.m2i.model.transaction;

import com.m2i.model.account.Account;
import java.math.BigDecimal;

public final class RefundCredit extends Refund {

    public RefundCredit(Account account, BigDecimal amount, String description) {
        super(account, amount, description);
    }
}
