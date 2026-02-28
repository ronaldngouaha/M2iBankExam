package com.m2i.service.impl;

import com.m2i.model.account.Account;
import com.m2i.model.transaction.*;
import com.m2i.service.RefundCreditService;
import com.m2i.utils.AccessType;

import java.math.BigDecimal;
import java.util.List;


public class RefundCreditServiceImpl implements RefundCreditService {

    private final BlockchainServiceImpl blockchainService;
    private final AccountValidationServiceImpl validationService;
    private final BalanceServiceImpl balanceService;
    public RefundCreditServiceImpl (BlockchainServiceImpl blockchainService, AccountValidationServiceImpl validationService) {
        this.blockchainService = blockchainService;
        this.validationService= validationService;
        this.balanceService = new BalanceServiceImpl(blockchainService, validationService);

    }

    @Override
    public RequestResponse<List<Operation>> doOperation(RefundCredit refund) {
        
        // Validation de l'opération de remboursement
        Account account= refund.getAccount();

        Blockchain blockchain = blockchainService.getBlockchain().getResponseValue();
        try {
            final RequestResponse <List<Operation>>[] result = new RequestResponse[1];

            validationService.lockAccount(account, ()->{

                if(!validationService.canOperate(account).getResponseValue()){
                    try {
                        blockchainService.lockBlockchain(blockchainService.getBlockchain().getResponseValue(), AccessType.WRITE,()->{
                            refund.setStatus(OperationStatus.FAILED);
                            refund.setDescription("Account is blocked");
                            blockchainService.recordOperation(refund);
                            result[0] = new RequestResponse<>(ResponseStatusCode.ACCOUNT_LOCKED, "Account is blocked. Refund operation failed.", List.of(refund));

                            System.out.println("Account is blocked. Refund operation failed.");
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        result[0]= new RequestResponse<>(ResponseStatusCode.INTERNAL_SERVER_ERROR, "Refund operation failed due to interruption", List.of(refund));
                    }

                    return;
                }
                BigDecimal balance = balanceService.doOperation(new Balance(account, "Check balance for refund credit")).getResponseValue();

                if (!validationService.hasSufficientBalance(balance, refund.getAmount()).getResponseValue()) {

                    try {
                        blockchainService.lockBlockchain(blockchain,AccessType.WRITE,()->{
                            refund.setStatus(OperationStatus.FAILED);
                            blockchainService.recordOperation(refund);
                            result[0]= new RequestResponse<>(ResponseStatusCode.INSUFFICIENT_FUNDS, "Insufficient balance. Refund operation failed.", List.of(refund));

                            System.out.println("Insufficient balance. Refund operation failed.");
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        result[0]= new RequestResponse<>(ResponseStatusCode.INTERNAL_SERVER_ERROR, "Refund operation failed due to interruption", List.of(refund));
                    }

                    return;
                }

                try {
                    blockchainService.lockBlockchain(blockchain,AccessType.WRITE,()->{
                        // Effectuer le remboursement
                        Debit debit = new Debit(account, refund.getAmount(), "Refund Credit (" + refund.getDescription() + ")");
                        debit.setStatus(OperationStatus.SUCCESS);
                        refund.setStatus(OperationStatus.SUCCESS);
                        // Enregistrer les opérations dans la blockchain

                        blockchainService.recordOperation(refund);
                        blockchainService.recordOperation(debit);
                        result[0]= new RequestResponse<>(ResponseStatusCode.SUCCESS, "Refund Credit operation successful.", List.of(refund, debit));

                        System.out.println("Refund Credit operation successful.");
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    result[0]= new RequestResponse<>(ResponseStatusCode.INTERNAL_SERVER_ERROR, "Refund operation failed due to interruption", List.of(refund));
                }

            });
            // Retourner la réponse de l'opération de remboursement
            return result[0];
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new RequestResponse<>(ResponseStatusCode.INTERNAL_SERVER_ERROR, "Refund operation failed due to interruption", List.of(refund));
        }

    }
}
