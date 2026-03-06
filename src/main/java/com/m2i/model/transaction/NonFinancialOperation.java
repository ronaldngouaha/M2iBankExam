package com.m2i.model.transaction;

import com.m2i.model.account.Account;

import java.time.LocalDateTime;

public sealed abstract class NonFinancialOperation <T extends Account> extends Operation   permits MiniStatement, Statement, Balance  {

    public NonFinancialOperation (T account , OperationType operationType, String description) {
        super(account,operationType, description);

    }



}
