package com.m2i.model.transaction;

import com.m2i.model.account.Account;

import java.math.BigDecimal;

public non-sealed class Debit  <T extends Account> extends FinancialOperation {

    public Debit (T account, BigDecimal amount, String description) {
        super(account, amount, OperationType.DEBIT, description);
    }

}
