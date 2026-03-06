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

public class RefundTransferServiceImplTest {

    @Test
    public void testSuccessRefundTransfer(){


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


        RefundTransfer refundTransfer = new RefundTransfer(transfer.getSender(), transfer.getReceiver(), transfer.getAmount(), "Refund transfer to John");
        RefundTransferServiceImpl refundTransferService = new RefundTransferServiceImpl(blockchainService, validationService);
        RequestResponse<List<FinancialOperation>> refundResponse = refundTransferService.doOperation(refundTransfer);
        System.out.println(refundResponse);
        Assertions.assertTrue(ResponseStatusCode.SUCCESS==refundResponse.getStatusCode());
        BigDecimal johnBalanceAfterRefund = balanceService.doOperation(new Balance(johnAccount,"After refund transfer")).getResponseValue();
        BigDecimal aliceBalanceAfterRefund = balanceService.doOperation(new Balance(aliceAccount,"After refund transfer")).getResponseValue();


        assert johnBalanceAfterRefund.equals(expectedJohnBalance.add(refundTransfer.getAmount())) : "Expected John's balance after refund: " + expectedJohnBalance.add(refundTransfer.getAmount()) + ", but got: " + johnBalanceAfterRefund;
        assert aliceBalanceAfterRefund.equals(expectedAliceBalance.subtract(refundTransfer.getAmount())) : "Expected Alice's balance after refund: " + expectedAliceBalance.subtract(refundTransfer.getAmount()) + ", but got: " + aliceBalanceAfterRefund;


    }
}
