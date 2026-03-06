package com.m2i.service;

import com.m2i.model.account.Account;
import com.m2i.model.account.AccountStatus;
import com.m2i.model.account.AccountType;
import com.m2i.model.client.*;
import com.m2i.model.transaction.*;
import com.m2i.service.impl.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TransferServiceImplTest {
    @Test
    public void testSuccessTransfer(){


        BlockchainServiceImpl blockchainService = new BlockchainServiceImpl(new Blockchain());
        AccountValidationServiceImpl validationService = new AccountValidationServiceImpl();

        BalanceServiceImpl balanceService= new BalanceServiceImpl(blockchainService, validationService);

        Address address = new Address("asd", "wew", City.NEW_YORK, Country.CANADA, 20);
        Customer john = new Customer("Doe", "John", "+535467223", "john@gmail.com", address);
        Customer alice = new Customer("Tremblay", "Alice", "+5145467223", "alice@gmail.com", address);

        alice.setClientStatus(ClientStatus.ACTIVE);
        john.setClientStatus(ClientStatus.ACTIVE);
        Account johnAccount = new Account(john, AccountType.CHECKING, AccountStatus.ACTIVE);
        Account aliceAccount = new Account(alice, AccountType.CHECKING, AccountStatus.ACTIVE);

        BigDecimal initialBalanceAlice = balanceService.doOperation(new Balance(aliceAccount,"Initial balance")).getResponseValue();
        Assertions.assertEquals(BigDecimal.valueOf(0), initialBalanceAlice);

        BigDecimal initialBalanceJohn = balanceService.doOperation(new Balance(johnAccount,"Initial balance")).getResponseValue();
        Assertions.assertEquals(BigDecimal.valueOf(0), initialBalanceJohn);


        CreditServiceImpl creditService = new CreditServiceImpl(blockchainService, validationService);
        Credit credit = new Credit(johnAccount, BigDecimal.valueOf(2000.20), "Appro compte");

        RequestResponse<Credit> creditResponse = creditService.doOperation(credit);
        System.out.println( creditResponse);
        Assertions.assertTrue(ResponseStatusCode.SUCCESS==creditResponse.getStatusCode());

        Transfer transfer = new Transfer(johnAccount, aliceAccount, BigDecimal.valueOf(500), "Transfer to Alice");


            // Assert
        TransferService transferService = new TransferServiceImpl(blockchainService, validationService);
        RequestResponse<List<FinancialOperation>> transferResponse = transferService.doOperation(transfer);
        System.out.println(transferResponse);
        Assertions.assertTrue(ResponseStatusCode.SUCCESS==transferResponse.getStatusCode());

         // Assert
        BigDecimal expectedJohnBalance = BigDecimal.valueOf(1500.20);
        BigDecimal expectedAliceBalance = BigDecimal.valueOf(500);
        BigDecimal actualJohnBalance = balanceService.doOperation(new Balance(johnAccount,"After transfer")).getResponseValue();
        BigDecimal actualAliceBalance = balanceService.doOperation(new Balance(aliceAccount,"After transfer")).getResponseValue();
        assert expectedJohnBalance.equals(actualJohnBalance) : "Expected John's balance: " + expectedJohnBalance + ", but got: " + actualJohnBalance;
        assert expectedAliceBalance.equals(actualAliceBalance) : "Expected Alice's balance: " + expectedAliceBalance + ", but got: " + actualAliceBalance;

    }
    @Test
    public void testFailedTransferDueToLockAccount(){


        BlockchainServiceImpl blockchainService = new BlockchainServiceImpl(new Blockchain());
        AccountValidationServiceImpl validationService = new AccountValidationServiceImpl();

        Address address = new Address("asd", "wew", City.NEW_YORK, Country.CANADA, 20);
        Customer john = new Customer("Doe", "John", "+535467223", "john@gmail.com", address);
        Customer alice = new Customer("Tremblay", "Alice", "+5145467223", "alice@gmail.com", address);
        john.setClientStatus(ClientStatus.ACTIVE);

        Account johnAccount = new Account(john, AccountType.CHECKING, AccountStatus.ACTIVE);
        Account aliceAccount = new Account(alice, AccountType.CHECKING, AccountStatus.ACTIVE);

        CreditServiceImpl creditService = new CreditServiceImpl(blockchainService, validationService);
        Credit credit = new Credit(johnAccount, BigDecimal.valueOf(2000.20), "Appro compte");

        RequestResponse<Credit> creditResponse = creditService.doOperation(credit);
        System.out.println( creditResponse);
        Assertions.assertTrue(ResponseStatusCode.SUCCESS==creditResponse.getStatusCode());

        Transfer transfer = new Transfer(johnAccount, aliceAccount, BigDecimal.valueOf(500), "Transfer to Alice");


            // Assert
        TransferService transferService = new TransferServiceImpl(blockchainService, validationService);
        RequestResponse<List<FinancialOperation>> transferResponse = transferService.doOperation(transfer);
        System.out.println(transferResponse);
        // ALice account is not active so the transfer should fail and the status code should be ACCOUNT_LOCKED
        Assertions.assertTrue(ResponseStatusCode.ACCOUNT_LOCKED==transferResponse.getStatusCode());


    }

    @Test
    public void testFailedTransferDueToInsufficentFund(){


        BlockchainServiceImpl blockchainService = new BlockchainServiceImpl(new Blockchain());
        AccountValidationServiceImpl validationService = new AccountValidationServiceImpl();

        Address address = new Address("asd", "wew", City.NEW_YORK, Country.CANADA, 20);
        Customer john = new Customer("Doe", "John", "+535467223", "john@gmail.com", address);
        Customer alice = new Customer("Tremblay", "Alice", "+5145467223", "alice@gmail.com", address);
        john.setClientStatus(ClientStatus.ACTIVE);
        alice.setClientStatus(ClientStatus.ACTIVE);

        Account johnAccount = new Account(john, AccountType.CHECKING, AccountStatus.ACTIVE);
        Account aliceAccount = new Account(alice, AccountType.CHECKING, AccountStatus.ACTIVE);


        CreditServiceImpl creditService = new CreditServiceImpl(blockchainService, validationService);
        Credit credit = new Credit(johnAccount, BigDecimal.valueOf(2000.20), "Appro compte");

        RequestResponse<Credit> creditResponse = creditService.doOperation(credit);
        System.out.println( creditResponse);
        Assertions.assertTrue(ResponseStatusCode.SUCCESS==creditResponse.getStatusCode());

        Transfer transfer = new Transfer(johnAccount, aliceAccount, BigDecimal.valueOf(2500), "Transfer to Alice");


            // Assert
        TransferService transferService = new TransferServiceImpl(blockchainService, validationService);
        RequestResponse<List<FinancialOperation>> transferResponse = transferService.doOperation(transfer);
        System.out.println(transferResponse);
        // ALice account is not active so the transfer should fail and the status code should be ACCOUNT_LOCKED
        Assertions.assertTrue(ResponseStatusCode.INSUFFICIENT_FUNDS==transferResponse.getStatusCode());


    }

    @Test
    void shouldHandleConcurrentTransfersCorrectly() throws Exception {

        BlockchainServiceImpl blockchainService = new BlockchainServiceImpl(new Blockchain());
        AccountValidationServiceImpl validationService = new AccountValidationServiceImpl();
        BalanceServiceImpl balanceService =
                new BalanceServiceImpl(blockchainService, validationService);

        CreditServiceImpl creditService =
                new CreditServiceImpl(blockchainService, validationService);

        TransferServiceImpl transferService =
                new TransferServiceImpl(blockchainService, validationService);


        Address address = new Address("asd", "wew", City.NEW_YORK, Country.CANADA, 20);
        Customer johnAccount = new Customer("Doe", "John", "+535467223", "john@gmail.com", address);
        Customer aliceAccount = new Customer("Tremblay", "Alice", "+5145467223", "alice@gmail.com", address);

        johnAccount.setClientStatus(ClientStatus.ACTIVE);
        aliceAccount.setClientStatus(ClientStatus.ACTIVE);
        Account john = new Account(johnAccount, AccountType.CHECKING, AccountStatus.ACTIVE);
        Account alice = new Account(aliceAccount, AccountType.CHECKING, AccountStatus.ACTIVE);




        // John initial credit = 10 000
        creditService.doOperation(
                new Credit(john, BigDecimal.valueOf(1000), "Initial credit"));

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {

            executor.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await(); // tous démarrent ensemble

                    Transfer transfer = new Transfer(
                            john,
                            alice,
                            BigDecimal.valueOf(100),
                            "Concurrent transfer");

                    RequestResponse<List<FinancialOperation>> response =
                            transferService.doOperation(transfer);

                    if (response.getStatusCode() == ResponseStatusCode.SUCCESS) {
                        successCount.incrementAndGet();
                    } else {
                        failureCount.incrementAndGet();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();   // attendre que tous soient prêts
        startLatch.countDown(); // démarrage simultané
        doneLatch.await();    // attendre fin

        executor.shutdown();

        BigDecimal finalJohn =
                balanceService.doOperation(new Balance(john, "final"))
                        .getResponseValue();

        BigDecimal finalAlice =
                balanceService.doOperation(new Balance(alice, "final"))
                        .getResponseValue();

        Assertions.assertEquals(BigDecimal.ZERO, finalJohn);
        Assertions.assertEquals(BigDecimal.valueOf(1000), finalAlice);
        System.out.println("Final balance of John: " + finalJohn);
        System.out.println("Final balance of Alice: " + finalAlice);

        System.out.println("Successful transfers: " + successCount.get());
        System.out.println("Failed transfers: " + failureCount.get());

        Assertions.assertEquals(10, successCount.get());
        Assertions.assertEquals(0, failureCount.get());
    }
}
