package com.m2i.service;

import com.m2i.model.transaction.Operation;

import java.math.BigDecimal;

public interface OperationService<O extends Operation<?>, R> {

    R doOperation(O operation);
}