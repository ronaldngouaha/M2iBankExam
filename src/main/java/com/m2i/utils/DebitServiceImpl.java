package com.m2i.utils;

import com.m2i.model.account.Account;
import com.m2i.model.transaction.Debit;
import com.m2i.model.transaction.Operation;
import com.m2i.model.transaction.TransactionStatus;
import com.m2i.service.DebitService;

import java.math.BigDecimal;
import java.util.List;

public class DebitServiceImpl implements DebitService {

    private BlockchainServiceImpl blockchainService;
    private AccountValidationServiceImpl validationService;
    public DebitServiceImpl (BlockchainServiceImpl blockchainService, AccountValidationServiceImpl validationService) {
        this.blockchainService = blockchainService;
        this.validationService= validationService;

    }

    @Override
    public void doOperation(Debit debit) {

        Account account= debit.getAccount();
        try {
            validationService.lockAccount(account, ()->{
                // Validate account status
                if (!validationService.canOperate(account)) {
                    debit.setStatus(TransactionStatus.FAILED);
                    debit.setDescription("Account is blocked");
                    // Record the failed operation in the blockchain for audit purposes
                    try {
                        blockchainService.lockBlockchain(blockchainService.getBlockchain(),AccessType.WRITE,()->{
                            blockchainService.recordOperation(debit);
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println("Account is blocked. Operation failed.");
                    return;
                }
                BigDecimal balance = blockchainService.computeBalance(account);

                // For a debit operation, we typically don't check for sufficient balance since it's an incoming transaction. However, if there are specific rules (e.g., debit limits), we can implement them here.
                if(!validationService.hasSufficientBalance(balance, debit.getAmount())) {
                    debit.setStatus(TransactionStatus.FAILED);
                    // Record the failed operation in the blockchain for audit purposes
                    debit.setDescription("Insufficient balance");
                    try {
                        blockchainService.lockBlockchain(blockchainService.getBlockchain(),AccessType.WRITE,()->{
                            blockchainService.recordOperation(debit);
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println("Insufficient balance. Operation failed.");
                    return;
                }
                debit.setStatus(TransactionStatus.SUCCESS);
                // Record the operation in the blockchain
                try {
                    blockchainService.lockBlockchain(blockchainService.getBlockchain(),AccessType.WRITE,()->{
                        blockchainService.recordOperation(debit);
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                System.out.println("Debit operation successful and recorded in the blockchain.");

            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
