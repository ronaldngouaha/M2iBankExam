package com.m2i.model.transaction;
import com.m2i.model.account.Account;
import java.math.BigDecimal;

public non-sealed class Transfer  <T extends Account> extends FinancialOperation {

    private T receiver;

    public Transfer(T sender, T receiver, BigDecimal amount, String description) {
        super(sender, receiver, amount, OperationType.TRANSFER, description);

    }

}

