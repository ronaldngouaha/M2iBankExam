package com.m2i.model.transaction;

import com.m2i.model.account.Account;

import java.util.List;

public class Balance {

    protected List<Block> blockchainBlocks;
    protected Account account;

    public Balance(Account account, List<Block> blockchainBlocks) {
        this.account = account;
        this.blockchainBlocks = blockchainBlocks;
    }

}
