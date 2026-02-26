package com.m2i.service;

import com.m2i.model.account.Account;
import com.m2i.model.transaction.Block;
import com.m2i.model.transaction.Operation;

import java.math.BigDecimal;
import java.util.List;

public interface BalanceService {
    BigDecimal computeBalance(Account account);

}
