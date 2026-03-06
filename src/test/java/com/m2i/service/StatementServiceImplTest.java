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
import java.time.LocalDateTime;
import java.util.List;

public class StatementServiceImplTest {

    @Test
    public void doOperationTest() {

        BlockchainServiceImpl blockchainService = new BlockchainServiceImpl(new Blockchain());
        AccountValidationServiceImpl validationService = new AccountValidationServiceImpl();

        Address address = new Address("asd", "wew", City.NEW_YORK, Country.CANADA, 20);
        Customer customer = new Customer("johs", "sfd", "+5145467223", "john@gmail.com", address);
        customer.setClientStatus(ClientStatus.ACTIVE);
        Account account = new Account(customer, AccountType.CHECKING, AccountStatus.ACTIVE);

        CreditServiceImpl creditService = new CreditServiceImpl(blockchainService, validationService);
        Credit credit = new Credit(account, BigDecimal.valueOf(2000.20), "Appro compte");
        creditService.doOperation(credit);

        DebitServiceImpl debitService = new DebitServiceImpl(blockchainService, validationService);
        Debit debit = new Debit(account, BigDecimal.valueOf(500), "Retrait");
        debitService.doOperation(debit);

        Statement statement = new Statement(account, LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusMinutes(20), 1000);
        StatementService statementService = new StatementServiceImpl(blockchainService, validationService);

        List<FinancialOperation> operations = statementService.doOperation(statement).getResponseValue();
        Assertions.assertEquals(2, operations.size());
    }

    @Test
    public void doOperationReturnsAccountLockedWhenAccountInactive() {

        BlockchainServiceImpl blockchainService = new BlockchainServiceImpl(new Blockchain());
        AccountValidationServiceImpl validationService = new AccountValidationServiceImpl();

        Address address = new Address("asd", "wew", City.NEW_YORK, Country.CANADA, 20);
        Customer customer = new Customer("johs", "sfd", "+5145467223", "john@gmail.com", address);
        Account account = new Account(customer, AccountType.CHECKING, AccountStatus.INACTIVE);

        Statement statement = new Statement(account, LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusMinutes(20), 1000);
        StatementService statementService = new StatementServiceImpl(blockchainService, validationService);

        RequestResponse<List<FinancialOperation>> response = statementService.doOperation(statement);
        Assertions.assertEquals(ResponseStatusCode.ACCOUNT_LOCKED, response.getStatusCode());
    }
}
