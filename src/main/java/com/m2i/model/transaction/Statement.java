package com.m2i.model.transaction;

import com.m2i.exception.IllegalParameterArgumentException;
import com.m2i.model.account.Account;

import java.time.LocalDateTime;

public non-sealed class Statement  <T extends Account> extends NonFinancialOperation {

    private  LocalDateTime statementStartDate;
    private  LocalDateTime statementEndDate;
    private  Integer maxEntry;

    public Statement(T account, LocalDateTime statementStartDate, LocalDateTime statementEndDate, Integer maxEntry) {

        super(account, OperationType.STATEMENT, "Statement for account: " + account.getAccountNumber() + " from " + statementStartDate + " to " + statementEndDate + " with max entries: " + maxEntry);

        if(statementEndDate.isBefore(statementStartDate)){
            throw  IllegalParameterArgumentException.forInvalid("statementEndDate", "Statement end date cannot be before start date");
        }

        this.statementStartDate = statementStartDate;
        this.statementEndDate = statementEndDate;
        this.maxEntry = maxEntry;


    }

    public LocalDateTime getStatementStartDate() {
        return statementStartDate;
    }

    public LocalDateTime getStatementEndDate() {
        return statementEndDate;
    }

    public Integer getMaxEntry() {
        return maxEntry;
    }

    public void setStatementStartDate(LocalDateTime statementStartDate) {
        this.statementStartDate = statementStartDate;
    }

    public void setStatementEndDate(LocalDateTime statementEndDate) {
        this.statementEndDate = statementEndDate;
    }

    public void setMaxEntry(Integer maxEntry) {
        this.maxEntry = maxEntry;
    }

}
