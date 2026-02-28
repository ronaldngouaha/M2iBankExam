package com.m2i.service;

import com.m2i.model.account.Account;
import com.m2i.model.account.AccountStatus;
import com.m2i.model.account.AccountType;
import com.m2i.model.client.*;
import com.m2i.service.impl.AccountValidationServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AccountValidationServiceImplTest {

    @Test
    public void testCanOperate(){
        AccountValidationService accountValidationService = new AccountValidationServiceImpl();
        Address address = new Address("asd","wew", City.NEW_YORK, Country.CANADA,20);
        Customer customer= new Customer("johs","sfd","+5145467223","john@gmail.com",address);

        Account account = new Account(customer, AccountType.CHECKING);
        assertFalse( accountValidationService.canOperate(account).getResponseValue());

        customer.setClientStatus(ClientStatus.ACTIVE);
        // account est toujours INACTIVE
        assertFalse( accountValidationService.canOperate(account).getResponseValue());

        account.setAccountStatus(AccountStatus.ACTIVE);
        customer.setClientStatus(ClientStatus.ACTIVE);
        // account est ACTIVE et client est ACTIVE
        assertTrue(accountValidationService.canOperate(account).getResponseValue());

    }
    @Test
    public void testFailedCanOperateDue(){
        AccountValidationService accountValidationService = new AccountValidationServiceImpl();
        Address address = new Address("asd","wew", City.NEW_YORK, Country.CANADA,20);
        Customer customer= new Customer("johs","sfd","+5145467223","john@gmail.com",address);

        Account account = new Account(customer, AccountType.CHECKING);
        assertFalse( accountValidationService.canOperate(account).getResponseValue());

        customer.setClientStatus(ClientStatus.ACTIVE);
        // account est toujours INACTIVE
        assertFalse( accountValidationService.canOperate(account).getResponseValue());

        account.setAccountStatus(AccountStatus.INACTIVE);
        customer.setClientStatus(ClientStatus.ACTIVE);
        // account est INACTIVE et client est ACTIVE
        assertFalse(accountValidationService.canOperate(account).getResponseValue());


        // account est ACTIVE et client est INACTIVE
        account.setAccountStatus(AccountStatus.ACTIVE);
        customer.setClientStatus(ClientStatus.INACTIVE);

        assertFalse(accountValidationService.canOperate(account).getResponseValue());

    }
}
