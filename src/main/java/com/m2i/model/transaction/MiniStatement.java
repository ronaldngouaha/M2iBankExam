package com.m2i.model.transaction;

import com.m2i.model.account.Account;

public non-sealed class MiniStatement  <T extends Account> extends  NonFinancialOperation {

    protected Integer numberOfTransactions;


    public MiniStatement (T account, Integer numberOfTransactions  ) {
        super(account, OperationType.MINI_STATEMENT, "Mini Statement for account: " + account.getAccountNumber()+" with "+numberOfTransactions+" transactions.");
        this.numberOfTransactions=numberOfTransactions;

    }



    public Integer getNumberOfTransactions() {
        return numberOfTransactions;
    }

}
