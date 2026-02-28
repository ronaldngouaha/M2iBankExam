package com.m2i.service;

import com.m2i.model.account.Account;
import com.m2i.model.transaction.Balance;
import com.m2i.model.transaction.RequestResponse;

import java.math.BigDecimal;
import java.util.function.Supplier;

public interface AccountValidationService {

     RequestResponse<Boolean> canOperate(Account account);
    RequestResponse<Boolean>  hasSufficientBalance(BigDecimal balance, BigDecimal amount);
    void  lockAccount(Account acc1, Account acc2, Runnable action) throws InterruptedException;
    void  lockAccount(Account acc1, Runnable action) throws InterruptedException;
}
