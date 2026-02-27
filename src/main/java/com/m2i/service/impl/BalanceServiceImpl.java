package com.m2i.service.impl;

import com.m2i.model.account.Account;
import com.m2i.model.transaction.Balance;
import com.m2i.model.transaction.Operation;
import com.m2i.model.transaction.OperationStatus;
import com.m2i.model.transaction.OperationType;
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
    public BigDecimal doOperation(Balance balance) {

        Account account = balance.getAccount();

        if(!validationService.canOperate(account)){
            throw new IllegalStateException("Account is not in a valid state for balance computation.");
        }

        List<Operation> operations = blockchainService.getOperationsForAccount(account);
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


        return creditTotal.subtract(debitTotal);
    }
}
