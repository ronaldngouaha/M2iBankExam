package com.m2i.utils;

import com.m2i.model.transaction.MiniStatement;
import com.m2i.model.transaction.Operation;
import com.m2i.service.MiniStatementService;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MiniStatementServiceImpl implements MiniStatementService {

    private final BlockchainServiceImpl blockchainService;
    public MiniStatementServiceImpl (BlockchainServiceImpl blockchainService) {
        this.blockchainService = blockchainService;
    }

    @Override
    public List<Operation> doOperation(MiniStatement miniStatement) {

        return blockchainService.getOperationsForAccount(miniStatement.getAccount())
                .stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Operation::getTransactionDate).reversed())
                .limit(miniStatement.getNumberOfTransactions())
                .collect(Collectors.toList());

    }
}
