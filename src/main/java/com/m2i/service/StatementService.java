package com.m2i.service;

import com.m2i.model.transaction.Operation;
import com.m2i.model.transaction.Statement;

import java.util.List;

public interface StatementService extends OperationService <Statement, List<Operation>> {

}
