package com.m2i.service.impl;

import com.m2i.model.account.Account;
import com.m2i.model.transaction.Operation;
import com.m2i.model.transaction.Statement;
import com.m2i.service.StatementService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class StatementServiceImpl implements StatementService  {

    private final BlockchainServiceImpl blockchainService;
    private final AccountValidationServiceImpl validationService;
    public StatementServiceImpl (BlockchainServiceImpl blockchainService, AccountValidationServiceImpl validationService) { this.blockchainService = blockchainService; this.validationService= validationService; }

    @Override
    public List<Operation> doOperation(Operation statement) {

        Account account = statement.getAccount();

        if(!validationService.canOperate(account)){
            throw new IllegalStateException("Account is not in a valid state for balance computation.");
        }

        LocalDateTime start = statement.getStatementStartDate();
        LocalDateTime end = statement.getStatementEndDate();
        int max = statement.getMaxEntry();

        blockchainService.recordOperation(statement);

        return blockchainService.getOperationsForAccount(statement.getAccount())
                .stream()
                .filter(Objects::nonNull)
                .filter(op -> {
                    LocalDateTime date = op.getTransactionDate();
                    return (date.isEqual(start) || date.isAfter(start)) &&
                            (date.isEqual(end)   || date.isBefore(end));
                })
                .sorted((op1, op2) -> op2.getTransactionDate().compareTo(op1.getTransactionDate())) // tri décroissant
                .limit(max)
                .toList();
    }

}
