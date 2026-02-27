package com.m2i.service;

import com.m2i.model.account.Account;
import com.m2i.model.account.AccountStatus;
import com.m2i.model.account.AccountType;
import com.m2i.model.client.*;
import com.m2i.model.transaction.*;
import com.m2i.service.impl.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MiniStatementServiceImplTest {
    @Test
    public void doOperationTest(){

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

        CreditServiceImpl creditService2 = new CreditServiceImpl(blockchainService, validationService);
        Credit credit2 = new Credit(account, BigDecimal.valueOf(2000.20), "Appro compte");
        creditService2.doOperation(credit2);


        DebitServiceImpl debitService3 = new DebitServiceImpl(blockchainService, validationService);
        Debit debit3 = new Debit(account, BigDecimal.valueOf(500), "Retrait");
        debitService3.doOperation(debit3);

        DebitServiceImpl debitService2 = new DebitServiceImpl(blockchainService, validationService);
        Debit debit2 = new Debit(account, BigDecimal.valueOf(500), "Retrait");
        debitService2.doOperation(debit2);


        DebitServiceImpl debitService4 = new DebitServiceImpl(blockchainService, validationService);
        Debit debit4 = new Debit(account, BigDecimal.valueOf(500), "Retrait");
        debitService4.doOperation(debit4);


        MiniStatement miniStatement= new MiniStatement(account, 5);

        MiniStatementService miniStatementService = new MiniStatementServiceImpl(blockchainService,validationService);

        List<Operation> operations= miniStatementService.doOperation(miniStatement);
        operations.forEach(operation -> {
            System.out.println("Type: " + operation.getType());
            System.out.println("Amount: " + operation.getAmount());
            System.out.println("Description: " + operation.getDescription());
            System.out.println("Date: " + operation.getTransactionDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            System.out.println("-------------------------");
        });
        assertEquals(5, operations.size());




    }
}
