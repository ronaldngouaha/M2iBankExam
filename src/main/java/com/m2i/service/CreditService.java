package com.m2i.service;

import com.m2i.model.transaction.Credit;
import com.m2i.model.transaction.Operation;

import java.util.List;

public interface CreditService {
    void doOperation(Credit credit);

}
