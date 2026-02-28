package com.m2i.service.impl;

import com.m2i.model.account.Account;
import com.m2i.model.transaction.*;
import com.m2i.service.CreditService;
import com.m2i.utils.AccessType;

public class CreditServiceImpl implements CreditService {


    private final BlockchainServiceImpl blockchainService;
    private final AccountValidationServiceImpl validationService;
    private final BalanceServiceImpl balanceService;
    public CreditServiceImpl (BlockchainServiceImpl blockchainService, AccountValidationServiceImpl validationService) {
        this.blockchainService = blockchainService;
        this.validationService= validationService;
        this.balanceService = new BalanceServiceImpl(blockchainService, validationService);

    }
    @Override
    public RequestResponse<Credit> doOperation(Credit credit) {

        Account account=credit.getAccount();

        Blockchain blockchain = blockchainService.getBlockchain().getResponseValue();
        try {
            final RequestResponse <Credit>[] result = new RequestResponse[1];

            validationService.lockAccount(account, ()->{

                // Validate account status
                        if (!validationService.canOperate(account).getResponseValue()) {

                            try {
                                blockchainService.lockBlockchain(blockchain, AccessType.WRITE,()->{
                                    credit.setStatus(OperationStatus.FAILED);
                                    credit.setDescription("Account is blocked");
                                    blockchainService.recordOperation(credit);
                                    result[0] = new RequestResponse<>(ResponseStatusCode.ACCOUNT_LOCKED, "Account is not valid for credit operation", credit);
                                });
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                result[0]= new RequestResponse<>(ResponseStatusCode.INTERNAL_SERVER_ERROR, "Failed to record operation due to interruption", credit);
                            }

                            return;
                        }

                                try {
                                    blockchainService.lockBlockchain(blockchain,AccessType.WRITE,()->{

                                        credit.setStatus(OperationStatus.SUCCESS);
                                        // Record the credit in the blockchain
                                        blockchainService.recordOperation(credit);
                                        result[0] = new RequestResponse<>(ResponseStatusCode.SUCCESS, "Credit operation completed successfully", credit);
                                    });
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    result[0]= new RequestResponse<>(ResponseStatusCode.INTERNAL_SERVER_ERROR, "Failed to record operation due to interruption", credit);
                                }

                    });
                 return result[0];

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return new RequestResponse<>(ResponseStatusCode.INTERNAL_SERVER_ERROR, "Failed to acquire lock on account due to interruption", credit);
                }


    }



}
