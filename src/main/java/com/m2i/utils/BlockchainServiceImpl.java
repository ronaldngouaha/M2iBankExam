package com.m2i.utils;

import com.m2i.model.account.Account;
import com.m2i.model.account.AccountType;
import com.m2i.model.transaction.*;
import com.m2i.service.BlockchainService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class BlockchainServiceImpl implements BlockchainService {

    private final Blockchain blockchain;


    public BlockchainServiceImpl(Blockchain blockchain) {
        this.blockchain = blockchain;

    }

    @Override
    public List<Operation> getOperationsForAccount(Account account) {

            return blockchain.getBlocks().stream()
                    .map(Block::getOperation)
                    .filter(Objects::nonNull)
                    .filter(operation -> operation.getAccount()!=null && operation.getAccount().equals(account))

                    .collect(Collectors.toList());

    }

    @Override
    public void recordOperation(Operation operation) {

                Block  lastBlock = blockchain.getLastBlock();
        Block newBlock = null;
        try {
            newBlock = new Block(operation, lastBlock != null ? lastBlock.getHash() : "0");
        } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        }
        blockchain.addBlock(newBlock);


    }

    @Override
    public Blockchain getBlockchain() {

        return blockchain;

    }
    @Override
    public List<Block> getAllBlocks() {

        return blockchain.getBlocks();

    }

    @Override
    public void lockBlockchain(Blockchain blockchain, AccessType access, Runnable action) throws InterruptedException {

        boolean acquired = false;

        while (!acquired) {

            boolean locked = (access == AccessType.WRITE)
                    ? blockchain.getLock().writeLock().tryLock()
                    : blockchain.getLock().readLock().tryLock();

            if (locked) {
                try {
                    acquired = true;
                    action.run();
                } finally {
                    if (access == AccessType.WRITE) {
                        blockchain.getLock().writeLock().unlock();
                    } else {
                        blockchain.getLock().readLock().unlock();
                    }
                }
            }

            if (!acquired) {
                Thread.sleep(1);
            }
        }
    }



    @Override
    public BigDecimal computeBalance(Account account) {

                    List<Operation> operations = getOperationsForAccount(account);
                BigDecimal credits= operations.stream()
                        .filter(op -> op.getType() == TransactionType.CREDIT)
                        .filter(operation -> operation.getStatus()== TransactionStatus.SUCCESS)
                        .map(Operation::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal debits= operations.stream()
                        .filter(op -> op.getType() == TransactionType.DEBIT)
                        .filter(operation -> operation.getStatus()== TransactionStatus.SUCCESS)
                        .map(Operation::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                return credits.subtract(debits);

    }

}
