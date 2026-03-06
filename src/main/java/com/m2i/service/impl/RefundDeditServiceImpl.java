package com.m2i.service.impl;

import com.m2i.model.account.Account;
import com.m2i.model.transaction.*;
import com.m2i.service.RefundDebitService;
import com.m2i.utils.AccessType;

import java.util.List;

public class RefundDeditServiceImpl implements RefundDebitService {


    private final BlockchainServiceImpl blockchainService;
    private final AccountValidationServiceImpl validationService;
    private final BalanceServiceImpl balanceService;
    public RefundDeditServiceImpl(BlockchainServiceImpl blockchainService, AccountValidationServiceImpl validationService) {
        this.blockchainService = blockchainService;
        this.validationService= validationService;
        this.balanceService = new BalanceServiceImpl(blockchainService, validationService);


    }


    @Override
    public RequestResponse<List<FinancialOperation>> doOperation(RefundDebit refund) {



        // Validation de l'opération de remboursement
        Account account= refund.getAccount();

        Blockchain blockchain= blockchainService.getBlockchain().getResponseValue();

        try {
            final RequestResponse <List<FinancialOperation>>[] result = new RequestResponse[1];

            validationService.lockAccount(account, ()->{

                if(!validationService.canOperate(account).getResponseValue()){

                    try {
                        blockchainService.lockBlockchain(blockchain, AccessType.WRITE,()->{
                            refund.setStatus(OperationStatus.FAILED);
                            refund.setDescription("Account is blocked");
                            blockchainService.recordOperation(refund);
                            result[0]= new RequestResponse<>(ResponseStatusCode.ACCOUNT_LOCKED, "Account is blocked", List.of(refund));
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        result[0]= new RequestResponse<>(ResponseStatusCode.INTERNAL_SERVER_ERROR, "Failed to record failed refund operation due to interruption", List.of(refund));
                    }
                    System.out.println("Refund failed: Account is blocked");
                    return;
                }
                try {
                    blockchainService.lockBlockchain(blockchain,AccessType.WRITE,()->{

                        // Effectuer le remboursement
                        Credit credit = new Credit(account, refund.getAmount(), "Refund Debit (" + refund.getDescription() + ")");
                        credit.setStatus(OperationStatus.SUCCESS);
                        refund.setStatus(OperationStatus.SUCCESS);
                        // Enregistrer les opérations dans la blockchain
                       blockchainService.recordOperation(refund);
                        blockchainService.recordOperation(credit);
                            result[0]= new RequestResponse<>(ResponseStatusCode.SUCCESS, "Refund processed successfully", List.of(refund, credit));

                        System.out.println("Refund successful: " + refund.getAmount() + " refunded to account " + account.getAccountId());
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    result[0]= new RequestResponse<>(ResponseStatusCode.INTERNAL_SERVER_ERROR, "Failed to process refund due to interruption", List.of(refund));
                }


            });
            return result[0];
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new RequestResponse<>(ResponseStatusCode.INTERNAL_SERVER_ERROR, "Failed to process refund due to interruption", List.of(refund));
        }

    }
}
