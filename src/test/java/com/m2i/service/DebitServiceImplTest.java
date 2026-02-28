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

public class DebitServiceImplTest {
    @Test
    public void testSuccessDoOperation() {
        // Arrange
        BlockchainServiceImpl blockchainService = new BlockchainServiceImpl(new Blockchain());
        AccountValidationServiceImpl validationService = new AccountValidationServiceImpl();

        BalanceServiceImpl balanceService = new BalanceServiceImpl(blockchainService, validationService);

        Address address = new Address("asd", "wew", City.NEW_YORK, Country.CANADA, 20);
        Customer customer = new Customer("johs", "sfd", "+5145467223", "john@gmail.com", address);
        customer.setClientStatus(ClientStatus.ACTIVE);
        Account account = new Account(customer, AccountType.CHECKING, AccountStatus.ACTIVE);

        BigDecimal initialBalance = balanceService.doOperation(new Balance(account, "Initial balance")).getResponseValue();
        Assertions.assertEquals(BigDecimal.valueOf(0), initialBalance);

        CreditServiceImpl creditService = new CreditServiceImpl(blockchainService, validationService);
        Credit credit = new Credit(account, BigDecimal.valueOf(2000.20), "Appro compte");


        System.out.println( creditService.doOperation(credit));

        Assertions.assertEquals(BigDecimal.valueOf(2000.20), balanceService.doOperation(new Balance(account, "After credit")).getResponseValue());


        DebitServiceImpl debitService = new DebitServiceImpl(blockchainService, validationService);
        Debit debit = new Debit(account, BigDecimal.valueOf(500), "Retrait");

        System.out.println(debitService.doOperation(debit));

         // Assert
        BigDecimal expectedBalance = BigDecimal.valueOf(1500.20);
        BigDecimal actualBalance = balanceService.doOperation(new Balance(account, "After debit")).getResponseValue();
        Assertions.assertEquals(expectedBalance, actualBalance);

    }
    @Test
    public void testFailedOperationDueToAccountLocked() {
        // Arrange
        BlockchainServiceImpl blockchainService = new BlockchainServiceImpl(new Blockchain());
        AccountValidationServiceImpl validationService = new AccountValidationServiceImpl();

        BalanceServiceImpl balanceService = new BalanceServiceImpl(blockchainService, validationService);

        Address address = new Address("asd", "wew", City.NEW_YORK, Country.CANADA, 20);
        Customer customer = new Customer("johs", "sfd", "+5145467223", "john@gmail.com", address);
        customer.setClientStatus(ClientStatus.ACTIVE);
        Account account = new Account(customer, AccountType.CHECKING, AccountStatus.ACTIVE);

        BigDecimal initialBalance = balanceService.doOperation(new Balance(account, "Initial balance")).getResponseValue();
        Assertions.assertEquals(BigDecimal.valueOf(0), initialBalance);

        CreditServiceImpl creditService = new CreditServiceImpl(blockchainService, validationService);
        Credit credit = new Credit(account, BigDecimal.valueOf(2000.20), "Appro compte");


        System.out.println( creditService.doOperation(credit));

        Assertions.assertEquals(BigDecimal.valueOf(2000.20), balanceService.doOperation(new Balance(account, "After credit")).getResponseValue());


        DebitServiceImpl debitService = new DebitServiceImpl(blockchainService, validationService);
        Debit debit = new Debit(account, BigDecimal.valueOf(500), "Retrait");

        account.setAccountStatus(AccountStatus.INACTIVE);
        RequestResponse <Debit> response = debitService.doOperation(debit);
        System.out.println(response);

            // Assert
        Assertions.assertTrue(ResponseStatusCode.ACCOUNT_LOCKED==response.getStatusCode());





    }

    @Test
    public void testFailedOperationDueToInsufficentFund() {
        // Arrange
        BlockchainServiceImpl blockchainService = new BlockchainServiceImpl(new Blockchain());
        AccountValidationServiceImpl validationService = new AccountValidationServiceImpl();

        BalanceServiceImpl balanceService = new BalanceServiceImpl(blockchainService, validationService);

        Address address = new Address("asd", "wew", City.NEW_YORK, Country.CANADA, 20);
        Customer customer = new Customer("johs", "sfd", "+5145467223", "john@gmail.com", address);
        customer.setClientStatus(ClientStatus.ACTIVE);
        Account account = new Account(customer, AccountType.CHECKING, AccountStatus.ACTIVE);

        BigDecimal initialBalance = balanceService.doOperation(new Balance(account, "Initial balance")).getResponseValue();
        Assertions.assertEquals(BigDecimal.valueOf(0), initialBalance);

        CreditServiceImpl creditService = new CreditServiceImpl(blockchainService, validationService);
        Credit credit = new Credit(account, BigDecimal.valueOf(2000.20), "Appro compte");


        System.out.println( creditService.doOperation(credit));

        Assertions.assertEquals(BigDecimal.valueOf(2000.20), balanceService.doOperation(new Balance(account, "After credit")).getResponseValue());


        DebitServiceImpl debitService = new DebitServiceImpl(blockchainService, validationService);
        Debit debit = new Debit(account, BigDecimal.valueOf(3000), "Retrait");

        RequestResponse <Debit> response = debitService.doOperation(debit);
        System.out.println(response);

            // Assert
        Assertions.assertTrue(ResponseStatusCode.INSUFFICIENT_FUNDS==response.getStatusCode());





    }
}
