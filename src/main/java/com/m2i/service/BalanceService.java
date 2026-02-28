package com.m2i.service;

import com.m2i.model.transaction.Balance;
import com.m2i.model.transaction.RequestResponse;

import java.math.BigDecimal;

public interface BalanceService  extends OperationService <Balance, RequestResponse<BigDecimal>> {


}
