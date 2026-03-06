package com.m2i.model.transaction;

import com.m2i.model.account.Account;

import java.math.BigDecimal;

public sealed abstract class FinancialOperation <T extends Account> extends Operation permits Debit, Credit, Transfer,Refund {

    public static final String AMOUNT = "amount";
    protected   BigDecimal amount;
    public FinancialOperation ( T account, BigDecimal amount, OperationType type, String description) {
        super(account, type, description);
         checkAttribute(AMOUNT, amount);
        this.amount = amount;


   }
    public BigDecimal getAmount() {
        return amount;
    }

}
