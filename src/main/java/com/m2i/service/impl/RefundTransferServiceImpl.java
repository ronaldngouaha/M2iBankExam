package com.m2i.service.impl;

import com.m2i.model.account.Account;
import com.m2i.model.transaction.*;
import com.m2i.service.RefundTransferService;
import com.m2i.utils.AccessType;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class RefundTransferServiceImpl implements RefundTransferService {

    private final BlockchainServiceImpl blockchainService;
    private final AccountValidationServiceImpl validationService;
    private final BalanceServiceImpl balanceService;
    public RefundTransferServiceImpl(BlockchainServiceImpl blockchainService, AccountValidationServiceImpl validationService) {
        this.blockchainService = blockchainService;
        this.validationService= validationService;
        this.balanceService = new BalanceServiceImpl(blockchainService, validationService);

    }

    @Override
    public RequestResponse<List<Operation>> doOperation(RefundTransfer rc) {


        // Validation de l'opération de remboursement
        Account sender= rc.getAccount();
        Account receiver= rc.getReceiver();

        Blockchain blockchain= blockchainService.getBlockchain().getResponseValue();

        try {
            final RequestResponse <List<Operation>>[] result = new RequestResponse[1];

            validationService.lockAccount(receiver, sender, ()->{

                if(!validationService.canOperate(sender).getResponseValue() || !validationService.canOperate(receiver).getResponseValue()){

                    try {
                        rc.setStatus(OperationStatus.FAILED);
                        rc.setDescription("Account(s) is blocked");

                          blockchainService.lockBlockchain(blockchain, AccessType.WRITE,()->{
                            blockchainService.recordOperation(rc);
                            result[0]= new RequestResponse<>(ResponseStatusCode.ACCOUNT_LOCKED, "RefundTransfer failed: Account(s) is blocked", List.of(rc));
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        result[0]= new RequestResponse<>(ResponseStatusCode.INTERNAL_SERVER_ERROR, "RefundTransfer failed due to interruption", List.of(rc));
                    }

                    System.out.println("RefundTransfer failed: Account(s) is blocked");
                    return;
                }

                BigDecimal receiverBalance = balanceService.doOperation(new Balance(receiver, "Check balance for refund transfer")).getResponseValue();
                if(!validationService.hasSufficientBalance(receiverBalance, rc.getAmount()).getResponseValue()){
                    try {
                        rc.setStatus(OperationStatus.FAILED);
                        rc.setDescription("Receiver has insufficient balance");

                        blockchainService.lockBlockchain(blockchain,AccessType.WRITE,()->{
                            blockchainService.recordOperation(rc);
                            result[0]= new RequestResponse<>(ResponseStatusCode.INSUFFICIENT_FUNDS, "RefundTransfer failed: Receiver has insufficient balance", List.of(rc));
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        result[0]= new RequestResponse<>(ResponseStatusCode.INTERNAL_SERVER_ERROR, "RefundTransfer failed due to interruption", List.of(rc));
                    }

                    System.out.println("RefundTransfer failed: Receiver has insufficient balance");
                    return;
                }
                try {
                    blockchainService.lockBlockchain(blockchain,AccessType.WRITE,()->{

                        // Effectuer le remboursement
                        Debit debit = new Debit(receiver, rc.getAmount(), "Refund Credit (" + rc.getDescription() + ")");
                        debit.setStatus(OperationStatus.SUCCESS);
                        Credit credit = new Credit(sender, rc.getAmount(), "Refund Debit (" + rc.getDescription() + ")");
                        credit.setStatus(OperationStatus.SUCCESS);
                        rc.setStatus(OperationStatus.SUCCESS);
                        // Enregistrer les opérations dans la blockchain dans le bon ordre pour la tracabilite
                        blockchainService.recordOperation(rc);
                        blockchainService.recordOperation(debit);
                        blockchainService.recordOperation(credit);

                        result[0]= new RequestResponse<>(ResponseStatusCode.SUCCESS, "RefundTransfer successful", Arrays.asList(rc, debit, credit));
                        System.out.println("RefundTransfer successful: " + rc.getAmount() + " from " + receiver.getAccountId() + " to " + sender.getAccountId());

                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    result[0]= new RequestResponse<>(ResponseStatusCode.INTERNAL_SERVER_ERROR, "RefundTransfer failed due to interruption", List.of(rc));
                }


            });
            return result[0];
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new RequestResponse<>(ResponseStatusCode.INTERNAL_SERVER_ERROR, "RefundTransfer failed due to interruption", List.of(rc));
        }

    }
}
