package com.m2i.service.impl;

import com.m2i.model.account.Account;
import com.m2i.model.transaction.Credit;
import com.m2i.model.transaction.Operation;
import com.m2i.model.transaction.OperationStatus;
import com.m2i.service.CreditService;
import com.m2i.utils.AccessType;

public class CreditServiceImpl implements CreditService {


    private final BlockchainServiceImpl blockchainService;
    private final AccountValidationServiceImpl validationService;

    public CreditServiceImpl (BlockchainServiceImpl blockchainService, AccountValidationServiceImpl validationService) {
        this.blockchainService = blockchainService;
        this.validationService= validationService;

    }
    @Override
    public Void doOperation(Operation credit) {

        Account account=credit.getAccount();

        System.out.println(String.format("Current thread: %s  try to credit account: %s , amount: %.2f", Thread.currentThread().getName(), account.getAccountNumber(), credit.getAmount()));

        try {
            validationService.lockAccount(account, ()->{

                        // Validate account status
                        if (!validationService.canOperate(account)) {
                            credit.setStatus(OperationStatus.FAILED);
                            credit.setDescription("Account is blocked");
                            try {
                                blockchainService.lockBlockchain(blockchainService.getBlockchain(), AccessType.WRITE,()->{
                                    blockchainService.recordOperation(credit);
                                });
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            System.out.println(String.format("Current thread: %s  failed to credit account: %s , amount: %.2f", Thread.currentThread().getName(), account.getAccountNumber(), credit.getAmount()));

                            return;
                        }

                credit.setStatus(OperationStatus.SUCCESS);
                        // Record the credit in the blockchain
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

        return null;

    }



}
