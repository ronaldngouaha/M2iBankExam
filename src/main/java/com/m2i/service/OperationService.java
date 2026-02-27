package com.m2i.service;

import com.m2i.model.transaction.Operation;

public interface OperationService<O extends Operation, R> {

    R doOperation(O operation);
}