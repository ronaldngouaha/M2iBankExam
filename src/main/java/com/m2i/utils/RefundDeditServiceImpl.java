package com.m2i.utils;

import com.m2i.model.account.Account;
import com.m2i.model.transaction.*;
import com.m2i.service.RefundService;

import java.math.BigDecimal;
import java.util.List;

public class RefundDeditServiceImpl implements RefundService {


    private BlockchainServiceImpl blockchainService;
    private AccountValidationServiceImpl validationService;
    public RefundDeditServiceImpl(BlockchainServiceImpl blockchainService, AccountValidationServiceImpl validationService) {
        this.blockchainService = blockchainService;
        this.validationService= validationService;

    }


    @Override
    public void doOperation(Refund refund) {

        if(! (refund instanceof RefundDebit rc)){
            throw new IllegalArgumentException("Refund must be of type RefundDebit");
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
                    System.out.println("Refund failed: Account is blocked");
                    return;
                }
                // Effectuer le remboursement
                Credit credit = new Credit(account, rc.getAmount(), "Refund Debit (" + rc.getDescription() + ")");
                credit.setStatus(TransactionStatus.SUCCESS);
                rc.setStatus(TransactionStatus.SUCCESS);
                // Enregistrer les opérations dans la blockchain
                try {
                    blockchainService.lockBlockchain(blockchainService.getBlockchain(),AccessType.WRITE,()->{
                       blockchainService.recordOperation(rc);
                        blockchainService.recordOperation(credit);
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }


                System.out.println("Refund successful: " + rc.getAmount() + " refunded to account " + account.getAccountId());
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


    }
}
