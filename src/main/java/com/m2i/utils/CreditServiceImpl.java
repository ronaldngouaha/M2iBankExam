package com.m2i.utils;

import com.m2i.model.account.Account;
import com.m2i.model.transaction.Credit;
import com.m2i.model.transaction.Operation;
import com.m2i.model.transaction.TransactionStatus;
import com.m2i.service.BlockchainService;
import com.m2i.service.CreditService;

import java.math.BigDecimal;
import java.util.List;

public class CreditServiceImpl implements CreditService {


    private BlockchainServiceImpl blockchainService;
    private AccountValidationServiceImpl validationService;

    public CreditServiceImpl (BlockchainServiceImpl blockchainService, AccountValidationServiceImpl validationService) {
        this.blockchainService = blockchainService;
        this.validationService= validationService;

    }
    @Override
    public void doOperation(Credit credit) {

        Account account=credit.getAccount();

        System.out.println(String.format("Current thread: %s  try to credit account: %s , amount: %.2f", Thread.currentThread().getName(), account.getAccountNumber(), credit.getAmount()));

        try {
            validationService.lockAccount(account, ()->{

                        // Validate account status
                        if (!validationService.canOperate(account)) {
                            credit.setStatus(TransactionStatus.FAILED);
                            credit.setDescription("Account is blocked");
                            try {
                                blockchainService.lockBlockchain(blockchainService.getBlockchain(),AccessType.WRITE,()->{
                                    blockchainService.recordOperation(credit);
                                });
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            System.out.println(String.format("Current thread: %s  failed to credit account: %s , amount: %.2f", Thread.currentThread().getName(), account.getAccountNumber(), credit.getAmount()));

                            return;
                        }

                        credit.setStatus(TransactionStatus.SUCCESS);
                        // Record the operation in the blockchain
                                try {
                                    blockchainService.lockBlockchain(blockchainService.getBlockchain(),AccessType.WRITE,()->{
                                        blockchainService.recordOperation(credit);
                                    });
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }

                System.out.println(String.format("Current thread: %s  successfully credit account: %s , amount: %.2f", Thread.currentThread().getName(), account.getAccountNumber(), credit.getAmount()));

                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }


    }
}
