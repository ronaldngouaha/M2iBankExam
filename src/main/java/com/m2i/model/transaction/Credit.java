package com.m2i.model.transaction;

import com.m2i.model.account.Account;

import java.math.BigDecimal;

public non-sealed class Credit  <T extends Account> extends FinancialOperation {

    public Credit (T account, BigDecimal amount, String description) {
        super(account, amount, OperationType.CREDIT, description);



    }

}
