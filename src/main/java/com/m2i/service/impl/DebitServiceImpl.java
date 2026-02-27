package com.m2i.service.impl;

import com.m2i.model.account.Account;
import com.m2i.model.transaction.Balance;
import com.m2i.model.transaction.Debit;
import com.m2i.model.transaction.OperationStatus;
import com.m2i.service.DebitService;
import com.m2i.utils.AccessType;

import java.math.BigDecimal;

public class DebitServiceImpl implements DebitService {

    private final BlockchainServiceImpl blockchainService;
    private final AccountValidationServiceImpl validationService;
    public DebitServiceImpl (BlockchainServiceImpl blockchainService, AccountValidationServiceImpl validationService) {
        this.blockchainService = blockchainService;
        this.validationService= validationService;

    }

    @Override
    public Void doOperation(Debit debit) {

        Account account= debit.getAccount();

        System.out.println(String.format("Current thread: %s  try to debit account: %s , amount: %.2f", Thread.currentThread().getName(), account.getAccountNumber(), debit.getAmount()));

        try {
            validationService.lockAccount(account, ()->{
                // Validate account status
                if (!validationService.canOperate(account)) {
                    debit.setStatus(OperationStatus.FAILED);
                    debit.setDescription("Account is blocked");
                    // Record the failed operation in the blockchain for audit purposes
                    try {
                        blockchainService.lockBlockchain(blockchainService.getBlockchain(), AccessType.WRITE,()->{
                            blockchainService.recordOperation(debit);
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println(String.format("Current thread: %s  failed to debit account: %s , amount: %.2f", Thread.currentThread().getName(), account.getAccountNumber(), debit.getAmount()));

                    return;
                }
                BigDecimal balance = new  BalanceServiceImpl (blockchainService, validationService).doOperation(new Balance(account,"Get balance for debit operation id "+debit.getOperationId()));

                // For a debit operation, we typically don't check for sufficient balance since it's an incoming transaction. However, if there are specific rules (e.g., debit limits), we can implement them here.
                if(!validationService.hasSufficientBalance(balance, debit.getAmount())) {
                    debit.setStatus(OperationStatus.FAILED);
                    // Record the failed operation in the blockchain for audit purposes
                    debit.setDescription("Insufficient balance");
                    try {
                        blockchainService.lockBlockchain(blockchainService.getBlockchain(),AccessType.WRITE,()->{
                            blockchainService.recordOperation(debit);
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println(String.format("Current thread: %s  fail to debit account: %s , amount: %.2f", Thread.currentThread().getName(), account.getAccountNumber(), debit.getAmount()));

                    return;
                }
                debit.setStatus(OperationStatus.SUCCESS);
                // Record the operation in the blockchain
                try {
                    blockchainService.lockBlockchain(blockchainService.getBlockchain(),AccessType.WRITE,()->{
                        blockchainService.recordOperation(debit);
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                System.out.println("Debit operation successful and recorded in the blockchain.");
                System.out.println(String.format("Current thread: %s  successfully to debit account: %s , amount: %.2f", Thread.currentThread().getName(), account.getAccountNumber(), debit.getAmount()));

            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return  null;
    }


}
