package com.m2i.model.transaction;
import com.m2i.model.account.Account;
import java.math.BigDecimal;

public non-sealed class Transfer  <T extends Account> extends FinancialOperation {


    private static final String RECEIVER = "RECEIVER";
    private T receiver;

    public Transfer(T sender, T receiver, BigDecimal amount, String description) {
        super(sender, amount, OperationType.TRANSFER, description);

        checkAttribute(RECEIVER, receiver);
        this.receiver = receiver;
    }

    public T getSender() {
        return (T) super.getAccount();
    }


    public T getReceiver() {
        return receiver;
    }
}