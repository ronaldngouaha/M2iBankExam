package com.m2i.service;

import com.m2i.model.transaction.Operation;
import com.m2i.model.transaction.RequestResponse;
import com.m2i.model.transaction.Transfer;

import java.util.List;


public interface TransferService  extends OperationService <Transfer  , RequestResponse<List<Operation>>>{


}
