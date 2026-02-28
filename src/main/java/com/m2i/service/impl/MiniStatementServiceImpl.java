package com.m2i.service.impl;

import com.m2i.model.account.Account;
import com.m2i.model.transaction.MiniStatement;
import com.m2i.model.transaction.Operation;
import com.m2i.model.transaction.RequestResponse;
import com.m2i.model.transaction.ResponseStatusCode;
import com.m2i.service.MiniStatementService;
import java.util.List;
import java.util.Objects;

public class MiniStatementServiceImpl implements MiniStatementService  {


    private final BlockchainServiceImpl blockchainService;
    private final AccountValidationServiceImpl validationService;

    public MiniStatementServiceImpl(BlockchainServiceImpl blockchainService, AccountValidationServiceImpl validationService) {
        this.blockchainService = blockchainService;
        this.validationService = validationService;
    }
    @Override
    public RequestResponse<List<Operation>> doOperation(MiniStatement miniStatement) {

        Account account = miniStatement.getAccount();

        if(!validationService.canOperate(account).getResponseValue()){
            return new RequestResponse<>(ResponseStatusCode.ACCOUNT_LOCKED, "Account is not valid for mini statement retrieval", null);
         //   throw new IllegalStateException("Account is not in a valid state for balance computation.");
        }

        blockchainService.recordOperation(miniStatement);

        return new RequestResponse<>(ResponseStatusCode.SUCCESS, "Mini statement retrieved successfully",
                blockchainService.getOperationsForAccount(miniStatement.getAccount()).getResponseValue()
                        .stream()
                        .filter(Objects::nonNull)
                        .sorted((op1, op2) -> op2.getTransactionDate().compareTo(op1.getTransactionDate())) // tri décroissant
                        .limit(miniStatement.getNumberOfTransactions())
                        .toList()
                      );

    }
}
