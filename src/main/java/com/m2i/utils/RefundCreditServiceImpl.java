package com.m2i.utils;

import com.m2i.model.account.Account;
import com.m2i.model.transaction.*;
import com.m2i.service.RefundService;
import java.math.BigDecimal;


public class RefundCreditServiceImpl implements RefundService {

    private BlockchainServiceImpl blockchainService;
    private AccountValidationServiceImpl validationService;
    public RefundCreditServiceImpl (BlockchainServiceImpl blockchainService, AccountValidationServiceImpl validationService) {
        this.blockchainService = blockchainService;
        this.validationService= validationService;
    }

    @Override
    public void doOperation(Refund refund) {

        if(! (refund instanceof RefundCredit rc)){
            throw new IllegalArgumentException("Refund must be of type RefundCredit");
        }

        // Validation de l'opération de remboursement
        Account account= refund.getAccount();

        try {
            validationService.lockAccount(account, ()->{

                if(!validationService.canOperate(account)){
                    rc.setStatus(TransactionStatus.FAILED);
                    rc.setDescription("Account is blocked");
                    try {
                        blockchainService.lockBlockchain(blockchainService.getBlockchain(),AccessType.WRITE,()->{
                            blockchainService.recordOperation(rc);
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println("Account is blocked. Refund operation failed.");
                    return;
                }
                BigDecimal balance = blockchainService.computeBalance(account);
                if (!validationService.hasSufficientBalance(balance, rc.getAmount())) {
                    rc.setStatus(TransactionStatus.FAILED);

                    try {
                        blockchainService.lockBlockchain(blockchainService.getBlockchain(),AccessType.WRITE,()->{
                            blockchainService.recordOperation(rc);
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println("Insufficient balance. Refund operation failed.");
                    return;
                }

                // Effectuer le remboursement
                Debit debit = new Debit(account, rc.getAmount(), "Refund Credit (" + rc.getDescription() + ")");
                debit.setStatus(TransactionStatus.SUCCESS);
                rc.setStatus(TransactionStatus.SUCCESS);
                // Enregistrer les opérations dans la blockchain
                try {
                    blockchainService.lockBlockchain(blockchainService.getBlockchain(),AccessType.WRITE,()->{
                        blockchainService.recordOperation(rc);
                        blockchainService.recordOperation(debit);
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                System.out.println("Refund Credit operation successful.");

            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
