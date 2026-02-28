package com.m2i.service;

import com.m2i.model.account.Account;
import com.m2i.model.account.AccountStatus;
import com.m2i.model.account.AccountType;
import com.m2i.model.client.*;
import com.m2i.model.transaction.Blockchain;
import com.m2i.model.transaction.Credit;
import com.m2i.service.impl.AccountValidationServiceImpl;
import com.m2i.service.impl.BlockchainServiceImpl;
import com.m2i.service.impl.CreditServiceImpl;
import com.m2i.service.impl.MultiThreadedMiner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class MultiThreadedMinerTest {

    @Test
    public void testMultiThreadedMiner() {
        // Given
        MultiThreadedMiner miner = new MultiThreadedMiner(5, 5 ); // 4 threads

        BlockchainServiceImpl blockchainService = new BlockchainServiceImpl(new Blockchain());
        AccountValidationServiceImpl validationService = new AccountValidationServiceImpl();


        Address address = new Address("asd", "wew", City.NEW_YORK, Country.CANADA, 20);
        Customer customer = new Customer("johs", "sfd", "+5145467223", "john@gmail.com", address);
        customer.setClientStatus(ClientStatus.ACTIVE);
        Account account = new Account(customer, AccountType.CHECKING, AccountStatus.ACTIVE);


        CreditServiceImpl creditService = new CreditServiceImpl(blockchainService, validationService);
        Credit credit = new Credit(account, BigDecimal.valueOf(2000.20), "Appro compte");
        creditService.doOperation(credit);

            // When
        try {
         MultiThreadedMiner.MiningResult miningResult= miner.mine(blockchainService.getBlockchain().toString());
            Assertions.assertNotNull(miningResult);
            Assertions.assertEquals("00000", miningResult.hash.substring(0, miner.getDifficulty()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
