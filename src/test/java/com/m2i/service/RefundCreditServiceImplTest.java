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

public class RefundCreditServiceImplTest {

    @Test
    public void testRefundCredit() {
        // Given
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
        Credit credit = new Credit(account, BigDecimal.valueOf(2000.20), "Appro compte");
        creditService.doOperation(credit);

        RefundCreditService refundCreditService = new RefundCreditServiceImpl(blockchainService, validationService);

        RefundCredit refundCredit = new RefundCredit(credit.getAccount(), credit.getAmount(), "Refund for credit operation");

        // Set up the refund object with necessary data

        // When
        refundCreditService.doOperation(refundCredit);

        // Then
        // Add assertions to verify the response
        Assertions.assertTrue( balanceService.doOperation(new Balance(account,"After refund")).getResponseValue().compareTo(BigDecimal.valueOf(0))==0);

    }
}
