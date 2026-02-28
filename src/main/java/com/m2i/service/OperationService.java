package com.m2i.service;

import com.m2i.model.transaction.Operation;
import com.m2i.model.transaction.RequestResponse;

public interface OperationService<O extends Operation, R extends RequestResponse> {

    R doOperation(O operation);
}