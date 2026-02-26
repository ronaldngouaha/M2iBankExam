package com.m2i.utils;

import com.m2i.model.account.Account;
import com.m2i.model.transaction.*;
import com.m2i.service.RefundService;

import java.math.BigDecimal;

public class RefundTransferServiceImpl implements RefundService {

    private BlockchainServiceImpl blockchainService;
    private AccountValidationServiceImpl validationService;
    public RefundTransferServiceImpl(BlockchainServiceImpl blockchainService, AccountValidationServiceImpl validationService) {
        this.blockchainService = blockchainService;
        this.validationService= validationService;
    }

    @Override
    public void doOperation(Refund refund) {

        if(! (refund instanceof RefundTransfer rc)){
            throw new IllegalArgumentException("Refund must be of type RefundTransfer");
        }

        // Validation de l'opération de remboursement
        Account sender= refund.getAccount();
        Account receiver= rc.getReceiver();


        try {
            validationService.lockAccount(receiver, sender, ()->{

                if(!validationService.canOperate(sender) || !validationService.canOperate(receiver)){
                    rc.setStatus(TransactionStatus.FAILED);
                    rc.setDescription("Account(s) is blocked");

                    try {
                        blockchainService.lockBlockchain(blockchainService.getBlockchain(),AccessType.WRITE,()->{
                            blockchainService.recordOperation(rc);
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println("RefundTransfer failed: Account(s) is blocked");
                    return;
                }

                BigDecimal receiverBalance = blockchainService.computeBalance(receiver);
                if(!validationService.hasSufficientBalance(receiverBalance, rc.getAmount())){
                    rc.setStatus(TransactionStatus.FAILED);
                    rc.setDescription("Receiver has insufficient balance");

                    try {
                        blockchainService.lockBlockchain(blockchainService.getBlockchain(),AccessType.WRITE,()->{
                            blockchainService.recordOperation(rc);
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println("RefundTransfer failed: Receiver has insufficient balance");
                    return;
                }
                // Effectuer le remboursement
                Debit debit = new Debit(receiver, rc.getAmount(), "Refund Credit (" + rc.getDescription() + ")");
                debit.setStatus(TransactionStatus.SUCCESS);
                Credit credit = new Credit(sender, rc.getAmount(), "Refund Debit (" + rc.getDescription() + ")");
                credit.setStatus(TransactionStatus.SUCCESS);
                rc.setStatus(TransactionStatus.SUCCESS);
                // Enregistrer les opérations dans la blockchain dans le bon ordre pour la tracabilite
                try {
                    blockchainService.lockBlockchain(blockchainService.getBlockchain(),AccessType.WRITE,()->{
                        blockchainService.recordOperation(rc);
                        blockchainService.recordOperation(debit);
                        blockchainService.recordOperation(credit);
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                System.out.println("RefundTransfer successful: " + rc.getAmount() + " from " + receiver.getAccountId() + " to " + sender.getAccountId());

            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }
}
