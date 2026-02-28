package com.m2i.service.impl;

import com.m2i.model.account.Account;
import com.m2i.model.transaction.*;
import com.m2i.service.BalanceService;
import com.m2i.service.DebitService;
import com.m2i.utils.AccessType;

import java.math.BigDecimal;

public class DebitServiceImpl implements DebitService {

    private final BlockchainServiceImpl blockchainService;
    private final AccountValidationServiceImpl validationService;
    private final BalanceServiceImpl balanceService;
    public DebitServiceImpl (BlockchainServiceImpl blockchainService, AccountValidationServiceImpl validationService) {
        this.blockchainService = blockchainService;
        this.validationService= validationService;
        this.balanceService = new BalanceServiceImpl(blockchainService, validationService);

    }

    @Override
    public RequestResponse<Debit> doOperation(Debit debit) {

        Account account= debit.getAccount();

        Blockchain blockchain = blockchainService.getBlockchain().getResponseValue();
        try {
            final RequestResponse <Debit>[] result = new RequestResponse[1];
            validationService.lockAccount(account, ()->{
                // Validate account status
                if (!validationService.canOperate(account).getResponseValue()) {
                    try {
                        blockchainService.lockBlockchain(blockchain, AccessType.WRITE,()->{
                            debit.setStatus(OperationStatus.FAILED);
                            debit.setDescription("Account is blocked");
                            // Record the failed operation in the blockchain for audit purposes
                            blockchainService.recordOperation(debit);
                            result [0]= new RequestResponse<>(ResponseStatusCode.ACCOUNT_LOCKED, "Account is not valid for debit operation", debit);

                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        result[0]= new RequestResponse<>(ResponseStatusCode.INTERNAL_SERVER_ERROR, "Failed to record operation due to interruption", debit);
                    }
                    return;
                }
                BigDecimal balance = balanceService.doOperation(new Balance(account,"Get balance for debit operation id "+debit.getOperationId())).getResponseValue();

                // For a debit operation, we typically don't check for sufficient balance since it's an incoming transaction. However, if there are specific rules (e.g., debit limits), we can implement them here.
                if(!validationService.hasSufficientBalance(balance, debit.getAmount()).getResponseValue()) {
                    try {
                        blockchainService.lockBlockchain(blockchain,AccessType.WRITE,()->{

                            debit.setStatus(OperationStatus.FAILED);
                            debit.setDescription("Insufficient balance");
                            // Record the failed operation in the blockchain for audit purposes
                            blockchainService.recordOperation(debit);
                            System.out.println(String.format("Current thread: %s  fail to debit account: %s , amount: %.2f", Thread.currentThread().getName(), account.getAccountNumber(), debit.getAmount()));
                            result [0]= new RequestResponse<>(ResponseStatusCode.INSUFFICIENT_FUNDS, "Insufficient balance for debit operation", debit);

                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        result [0]= new RequestResponse<>(ResponseStatusCode.INTERNAL_SERVER_ERROR, "Blockchain internat interruption", debit);

                    }

                    return;
                }
                try {
                    blockchainService.lockBlockchain(blockchain,AccessType.WRITE,()->{

                        debit.setStatus(OperationStatus.SUCCESS);
                        // Record the operation in the blockchain
                        blockchainService.recordOperation(debit);
                        System.out.println("Debit operation successful and recorded in the blockchain.");
                        System.out.println(String.format("Current thread: %s  successfully to debit account: %s , amount: %.2f", Thread.currentThread().getName(), account.getAccountNumber(), debit.getAmount()));
                        result [0]= new RequestResponse<>(ResponseStatusCode.SUCCESS, "Debit operation successful and recorded in the blockchain", debit);

                    });

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    result [0]= new RequestResponse<>(ResponseStatusCode.INTERNAL_SERVER_ERROR, "Failed to record operation due to interruption", debit);
                }

            });
            // Return the result of the operation after processing
            return result[0];

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new RequestResponse<>(ResponseStatusCode.INTERNAL_SERVER_ERROR, "Failed to record operation due to interruption", debit);

        }

    }


}
