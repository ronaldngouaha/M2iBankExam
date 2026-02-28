package com.m2i.service;


import com.m2i.model.transaction.Operation;
import com.m2i.model.transaction.Refund;
import com.m2i.model.transaction.RefundCredit;
import com.m2i.model.transaction.RequestResponse;

import java.util.List;


public interface RefundCreditService extends OperationService <RefundCredit,  RequestResponse<List<Operation>>> {

}
