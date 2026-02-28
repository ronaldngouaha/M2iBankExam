package com.m2i.service;

import com.m2i.model.transaction.Credit;
import com.m2i.model.transaction.Debit;
import com.m2i.model.transaction.RequestResponse;

public interface DebitService extends OperationService <Debit,  RequestResponse<Debit>> {

}
