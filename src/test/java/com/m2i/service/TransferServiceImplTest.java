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
        RequestResponse<List<Operation>> transferResponse = transferService.doOperation(transfer);
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
        RequestResponse<List<Operation>> transferResponse = transferService.doOperation(transfer);
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
        RequestResponse<List<Operation>> transferResponse = transferService.doOperation(transfer);
        System.out.println(transferResponse);
        // ALice account is not active so the transfer should fail and the status code should be ACCOUNT_LOCKED
        Assertions.assertTrue(ResponseStatusCode.INSUFFICIENT_FUNDS==transferResponse.getStatusCode());


    }
}
