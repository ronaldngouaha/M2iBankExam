package com.m2i.model.transaction;

import com.m2i.model.account.Account;

public class MiniStatement {
    protected Account account;
    protected Integer numberOfTransactions;

    public MiniStatement (Account account, Integer numberOfTransactions) {
        this.account = account;
        this.numberOfTransactions = numberOfTransactions;
    }

    public Account getAccount() {
        return account;
    }

    public Integer getNumberOfTransactions() {
        return numberOfTransactions;
    }

}
