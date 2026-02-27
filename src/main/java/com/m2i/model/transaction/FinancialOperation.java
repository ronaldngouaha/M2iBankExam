package com.m2i.model.transaction;

import com.m2i.model.account.Account;

import java.math.BigDecimal;

public sealed abstract class FinancialOperation <T extends Account> extends Operation permits Debit, Credit, Transfer,Refund {

    public FinancialOperation ( T account, BigDecimal amount, OperationType type, String description) {
        super(account, amount, type, description);
    }

    public FinancialOperation ( T sender, T receiver, BigDecimal amount, OperationType type, String description) {
        super(sender, receiver, amount, type, description);
    }


}
