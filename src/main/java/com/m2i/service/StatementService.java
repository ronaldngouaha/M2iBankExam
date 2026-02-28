package com.m2i.service;

import com.m2i.model.transaction.Credit;
import com.m2i.model.transaction.Operation;
import com.m2i.model.transaction.RequestResponse;
import com.m2i.model.transaction.Statement;

import java.util.List;

public interface StatementService extends OperationService <Statement,  RequestResponse< List<Operation>  >> {

}
