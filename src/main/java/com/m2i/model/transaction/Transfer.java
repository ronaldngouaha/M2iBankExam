package com.m2i.model.transaction;
import com.m2i.model.account.Account;
import java.math.BigDecimal;

public non-sealed class Transfer  <T extends Account> extends FinancialOperation {


    public Transfer(T sender, T receiver, BigDecimal amount, String description) {
        super(sender, receiver, amount, OperationType.TRANSFER, description);

    }

    public T getSender() {
        return (T) super.getAccount();
    }

    @Override
    public T getReceiver() {
        return (T) super.getReceiver();
    }
}

