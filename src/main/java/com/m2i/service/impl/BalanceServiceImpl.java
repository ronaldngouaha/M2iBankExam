package com.m2i.service.impl;

import com.m2i.model.account.Account;
import com.m2i.model.transaction.*;
import com.m2i.service.BalanceService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class BalanceServiceImpl  implements BalanceService  {

    private final BlockchainServiceImpl blockchainService;
    private final AccountValidationServiceImpl validationService;

    public BalanceServiceImpl(BlockchainServiceImpl blockchainService, AccountValidationServiceImpl validationService) {
        this.blockchainService = blockchainService;
        this.validationService = validationService;
    }

    @Override
    public RequestResponse<BigDecimal> doOperation(Balance balance) {

        Account account = balance.getAccount();

        if(!validationService.canOperate(account).getResponseValue()){
            return new RequestResponse<>(ResponseStatusCode.ACCOUNT_LOCKED, "Account is not valid for balance computation", null);
        }

        List<Operation> operations = blockchainService.getOperationsForAccount(account).responseValue;
        BigDecimal creditTotal =operations
                .stream()
                .filter(Objects::nonNull)
                .filter(operation ->  operation.getType().equals(OperationType.CREDIT) && operation.getStatus().equals(OperationStatus.SUCCESS))
                .map(Operation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal debitTotal = operations
                .stream()
                .filter(Objects::nonNull)
                .filter(operation -> operation.getType().equals(OperationType.DEBIT) && operation.getStatus().equals(OperationStatus.SUCCESS))
                .map(Operation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        blockchainService.recordOperation(balance);

        return new RequestResponse<>(ResponseStatusCode.SUCCESS, "Balance computed successfully", creditTotal.subtract(debitTotal));
    }
}
