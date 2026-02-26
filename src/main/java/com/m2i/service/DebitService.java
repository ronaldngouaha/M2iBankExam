package com.m2i.service;

import com.m2i.model.transaction.Debit;
import com.m2i.model.transaction.Operation;

import java.util.List;

public interface DebitService {
    void doOperation(Debit debit);


}
