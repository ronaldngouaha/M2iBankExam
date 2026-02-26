package com.m2i.service;

import com.m2i.model.account.Account;

import java.math.BigDecimal;

public interface AccountValidationService {

     boolean canOperate(Account account);
    boolean hasSufficientBalance(BigDecimal balance, BigDecimal amount);
    void  lockAccount(Account acc1, Account acc2, Runnable action) throws InterruptedException;
    void  lockAccount(Account acc1, Runnable action) throws InterruptedException;
}
