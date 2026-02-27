package com.m2i.model.transaction;

import com.m2i.model.account.Account;

import java.time.LocalDateTime;

public non-sealed class Statement  <T extends Account> extends NonFinancialOperation {


    public Statement(T account, LocalDateTime statementStartDate, LocalDateTime statementEndDate, Integer maxEntry) {

        super(account, OperationType.STATEMENT, statementStartDate, statementEndDate, maxEntry);
    }
}
