package com.m2i.service.impl;

import com.m2i.model.account.Account;
import com.m2i.model.transaction.*;
import com.m2i.service.StatementService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class StatementServiceImpl implements StatementService  {

    private final BlockchainServiceImpl blockchainService;
    private final AccountValidationServiceImpl validationService;
    public StatementServiceImpl (BlockchainServiceImpl blockchainService, AccountValidationServiceImpl validationService) { this.blockchainService = blockchainService; this.validationService= validationService; }

    @Override
    public RequestResponse<List<FinancialOperation>> doOperation(Statement statement) {

        Account account = statement.getAccount();

        if(!validationService.canOperate(account).getResponseValue()){
            return new RequestResponse<>(ResponseStatusCode.ACCOUNT_LOCKED, "Account is not valid for statement retrieval", null);
        }

        LocalDateTime start = statement.getStatementStartDate();
        LocalDateTime end = statement.getStatementEndDate();
        int max = statement.getMaxEntry();

        return new RequestResponse<>(
                 ResponseStatusCode.SUCCESS, "Statement generated successfully",
                blockchainService.getOperationsForAccount(statement.getAccount()).getResponseValue()
                        .stream()
                        .filter(Objects::nonNull)
                        .filter(op -> {
                            LocalDateTime date = op.getTransactionDate();
                            return (date.isEqual(start) || date.isAfter(start)) &&
                                    (date.isEqual(end)   || date.isBefore(end));
                        })
                        .sorted((op1, op2) -> op2.getTransactionDate().compareTo(op1.getTransactionDate())) // tri décroissant
                        .limit(max)
                        .toList()
        );
    }

}
