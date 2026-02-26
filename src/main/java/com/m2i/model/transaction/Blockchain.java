package com.m2i.model.transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Blockchain {

    private final ReentrantReadWriteLock lock= new ReentrantReadWriteLock();
    private final List<Block> chain = new ArrayList<>();

    public Blockchain() {
        chain.add(createGenesisBlock());
    }

    private Block createGenesisBlock() {
        try {
            return new Block(null, "0");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void addBlock(Block block) {
        chain.add(block);
    }

    public List<Block> getBlocks() {
        return Collections.unmodifiableList(chain);
    }

    public Block getLastBlock() {
        return chain.get(chain.size() - 1);
    }

    public ReentrantReadWriteLock    getLock() {
        return lock;
    }
}
