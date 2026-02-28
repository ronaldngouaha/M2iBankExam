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

public class RefundDeditServiceImplTest {

    @Test
    public void testRefundDebit() {
        BlockchainServiceImpl blockchainService = new BlockchainServiceImpl(new Blockchain());
        AccountValidationServiceImpl validationService = new AccountValidationServiceImpl();

        BalanceServiceImpl balanceService= new BalanceServiceImpl(blockchainService, validationService);

        Address address = new Address("asd", "wew", City.NEW_YORK, Country.CANADA, 20);
        Customer customer = new Customer("johs", "sfd", "+5145467223", "john@gmail.com", address);
        customer.setClientStatus(ClientStatus.ACTIVE);
        Account account = new Account(customer, AccountType.CHECKING, AccountStatus.ACTIVE);

        BigDecimal initialBalance = balanceService.doOperation(new Balance(account,"Initial balance")).getResponseValue();
        Assertions.assertEquals(BigDecimal.valueOf(0), initialBalance);


        CreditServiceImpl creditService = new CreditServiceImpl(blockchainService, validationService);
        Credit credit = new Credit(account, BigDecimal.valueOf(600), "Appro compte");
        creditService.doOperation(credit);

        DebitServiceImpl debitService = new DebitServiceImpl(blockchainService, validationService);
        Debit debit = new Debit(account, BigDecimal.valueOf(500), "Retrait");
        debitService.doOperation(debit);

        // Set up the refund object with necessary data

        RefundDebit refundDebit = new RefundDebit(debit.getAccount(), debit.getAmount(), "Refund for debit");
        RefundDeditServiceImpl refundDeditService = new RefundDeditServiceImpl(blockchainService, validationService);
        // When
        refundDeditService.doOperation(refundDebit);

        // Then
        // Add assertions to verify the response
        Assertions.assertEquals(BigDecimal.valueOf(600), balanceService.doOperation(new Balance(account,"After refund")).getResponseValue());

    }
}
