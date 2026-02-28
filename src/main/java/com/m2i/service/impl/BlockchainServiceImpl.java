package com.m2i.service.impl;

import com.m2i.model.account.Account;
import com.m2i.model.transaction.*;
import com.m2i.service.BlockchainService;
import com.m2i.utils.AccessType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlockchainServiceImpl implements BlockchainService {

    private final Blockchain blockchain;


    public BlockchainServiceImpl(Blockchain blockchain) {
        this.blockchain = blockchain;

    }

    @Override
    public  RequestResponse <List <Operation>>  getOperationsForAccount(Account account) {
        if (account == null) return new RequestResponse<>(ResponseStatusCode.ACCOUNT_NOT_FOUND, "Account cannot be null", new ArrayList<>());

        List<Operation> list = blockchain.getBlocks().stream()
                .map(Block::getOperation)
                .filter(Objects::nonNull)
                .filter(op -> account.equals(op.getAccount()))

                .toList();
        return new RequestResponse<>(ResponseStatusCode.SUCCESS, "Operations retrieved successfully", list);
    }


    @Override
    public RequestResponse<Operation> recordOperation(Operation operation) {

        Block  lastBlock = blockchain.getLastBlock();
        Block newBlock = null;
        try {
            newBlock = new Block(operation, lastBlock != null ? lastBlock.getHash() : "0");
        } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
            return new RequestResponse<>(ResponseStatusCode.INTERNAL_SERVER_ERROR, "Failed to record operation due to interruption", operation);
        }
        blockchain.addBlock(newBlock);

        return new RequestResponse<>(ResponseStatusCode.SUCCESS, "Operation recorded successfully", operation);

    }

    @Override
    public RequestResponse<Blockchain>   getBlockchain() {

        return new RequestResponse<>(ResponseStatusCode.SUCCESS, "Blockchain retrieved successfully", blockchain);

    }
    @Override
    public RequestResponse<List<Block>> getAllBlocks() {

        return new RequestResponse<>(ResponseStatusCode.SUCCESS, "Blocks retrieved successfully", blockchain.getBlocks());

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
            }else{

                System.out.println("==================================");
                System.out.println("Current Blockchain "+blockchain.toString()+" is blocked");
                System.out.println("==================================");
            }

            if (!acquired) {
                Thread.sleep(1);
            }
        }
    }




}
