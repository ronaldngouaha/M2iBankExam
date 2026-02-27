package com.m2i.service.impl;

import com.m2i.model.account.Account;
import com.m2i.model.transaction.MiniStatement;
import com.m2i.model.transaction.Operation;
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
    public List<Operation> doOperation(MiniStatement miniStatement) {

        Account account = miniStatement.getAccount();

        if(!validationService.canOperate(account)){
            throw new IllegalStateException("Account is not in a valid state for balance computation.");
        }

        blockchainService.recordOperation(miniStatement);

        return blockchainService.getOperationsForAccount(miniStatement.getAccount())
                .stream()
                .filter(Objects::nonNull)
                .sorted((op1, op2) -> op2.getTransactionDate().compareTo(op1.getTransactionDate())) // tri décroissant
                .limit(miniStatement.getNumberOfTransactions())
                .toList();

    }
}
