package com.m2i.service;

import com.m2i.model.transaction.*;

import java.util.List;

public interface StatementService extends OperationService <Statement,  RequestResponse< List<FinancialOperation>  >> {

}
