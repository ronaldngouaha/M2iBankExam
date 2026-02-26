package com.m2i.model.transaction;

import com.m2i.model.transaction.Operation;
import com.m2i.utils.MultiThreadedMiner;

public class Block {

    private final Operation operation;
    private final String previousHash;
    private final String hash;
    private Long nonce;
    private static final int DIFFICULTY= 5;
    private static final int MAX_POOL= 2;

    public Block(Operation operation, String previousHash)  throws InterruptedException{
        this.operation = operation;
        this.previousHash = previousHash;

        MultiThreadedMiner miner = new MultiThreadedMiner(DIFFICULTY, MAX_POOL);
        MultiThreadedMiner.MiningResult result= miner.mine(buildData());

        this.hash = result.hash;
        this.nonce = result.nonce;

    }

    public Long getNonce() {
        return nonce;
    }
    public Operation getOperation() { return operation; }
    public String getPreviousHash() { return previousHash; }
    public String getHash() { return hash; }

    private String buildData() {

        if (operation == null) {
            // GENESIS BLOCK
            return "GENESIS_BLOCK" + previousHash;
        }

        return operation.getOperationId()
                + operation.getTransactionDate()
                + operation.getAmount()
                + operation.getType()
                + previousHash;
    }

    @Override
    public String toString() {
        return "Block{" +
                "operation=" + operation +
                ", previousHash='" + previousHash + '\'' +
                ", hash='" + hash + '\'' +
                ", nonce=" + nonce +
                '}';
    }
}
