package com.m2i.model.transaction;

import com.m2i.model.account.Account;


public non-sealed class Balance <T extends Account> extends NonFinancialOperation  {

    public Balance (T account , String description) {
            super(account, OperationType.BALANCE, description);
        }

    }
