package com.m2i.utils;

import com.m2i.model.transaction.MiniStatement;
import com.m2i.model.transaction.Operation;
import com.m2i.model.transaction.Statement;
import com.m2i.service.MiniStatementService;
import com.m2i.service.StatementService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class StatementServiceImpl implements StatementService {

    private BlockchainServiceImpl blockchainService;
    public StatementServiceImpl(BlockchainServiceImpl blockchainService) {
        this.blockchainService = blockchainService;
    }
    @Override
    public List<Operation> doOperation(Statement statement) {

        LocalDateTime start = statement.getStatementStartDate();
        LocalDateTime end = statement.getStatementEndDate();
        int max = statement.getMaxEntry();

        return blockchainService.getOperationsForAccount(statement.getAccount())
                .stream()
                .filter(Objects::nonNull)
                .filter(op -> {
                    LocalDateTime date = op.getTransactionDate();
                    return (date.isEqual(start) || date.isAfter(start)) &&
                            (date.isEqual(end)   || date.isBefore(end));
                })
                .sorted(Comparator.comparing(Operation::getTransactionDate).reversed())
                .limit(max)
                .toList();
    }

}
