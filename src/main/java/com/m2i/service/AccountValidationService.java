package com.m2i.service;

import com.m2i.model.account.Account;

import java.math.BigDecimal;

public interface AccountValidation {

    boolean canOperate(Account account);
    boolean hasSufficientBalance(BigDecimal balance, BigDecimal amount);
}
