package com.m2i.service;

import com.m2i.model.account.Account;
import com.m2i.model.transaction.Block;
import com.m2i.model.transaction.Blockchain;
import com.m2i.model.transaction.Operation;
import com.m2i.utils.AccessType;

import java.math.BigDecimal;
import java.util.List;

public interface BlockchainService {

     List<Operation> getOperationsForAccount(Account account);
     void recordOperation(Operation operation);
     Blockchain getBlockchain();
     BigDecimal computeBalance (Account account);
     List<Block>getAllBlocks();
     void lockBlockchain(Blockchain blockchain, AccessType access , Runnable action ) throws  InterruptedException;
}
