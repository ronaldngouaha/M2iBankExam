package com.m2i.service.impl;
import com.m2i.model.account.Account;
import com.m2i.model.transaction.*;
import com.m2i.service.TransferService;
import com.m2i.utils.AccessType;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class TransferServiceImpl implements TransferService {
    private final BlockchainServiceImpl blockchainService;
    private final AccountValidationServiceImpl validationService;
    private final BalanceServiceImpl balanceService;
    public TransferServiceImpl (BlockchainServiceImpl blockchainService, AccountValidationServiceImpl validationService) {
        this.blockchainService = blockchainService;
        this.validationService= validationService;
        this.balanceService = new BalanceServiceImpl(blockchainService, validationService);

    }

    @Override
    public RequestResponse<List<Operation>> doOperation(Transfer transfer) {
        Account sender= transfer.getAccount();
        Account receiver = transfer.getReceiver();

        try {
            final RequestResponse <List<Operation>>[] result = new RequestResponse[1];

            validationService.lockAccount(sender, receiver, ()->{


                BigDecimal amount = transfer.getAmount();
                // Validate recipient account status
                // Validate account status
                if (!validationService.canOperate(sender).getResponseValue() || !validationService.canOperate(receiver).getResponseValue()) {
                    try {
                        blockchainService.lockBlockchain(blockchainService.getBlockchain().getResponseValue(), AccessType.WRITE,()->{
                            transfer.setStatus(OperationStatus.FAILED);
                            transfer.setDescription("Account is blocked");
                            // Record the failed operation in the blockchain for audit purposes
                            blockchainService.recordOperation(transfer);
                            result[0] = new RequestResponse<>(ResponseStatusCode.ACCOUNT_LOCKED, "One or both accounts are blocked", List.of(transfer));
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        result[0]= new RequestResponse<>(ResponseStatusCode.INTERNAL_SERVER_ERROR, "Transfer failed due to interruption", List.of(transfer));
                    }
                    System.out.println("Transfer failed: Account is blocked");
                    return;
                }
                BigDecimal senderBalance = balanceService.doOperation(new Balance(sender,"Check balance for transfer")).getResponseValue();
                // For a debit operation, we typically don't check for sufficient balance since it's an incoming transaction. However, if there are specific rules (e.g., debit limits), we can implement them here.
                if(!validationService.hasSufficientBalance(senderBalance, amount).getResponseValue()) {

                    try {
                        blockchainService.lockBlockchain(blockchainService.getBlockchain().getResponseValue(),AccessType.WRITE,()->{
                            transfer.setStatus(OperationStatus.FAILED);
                            transfer.setDescription("Insufficient balance");
                            // Record the failed operation in the blockchain for audit purposes
                            blockchainService.recordOperation(transfer);
                                result[0] = new RequestResponse<>(ResponseStatusCode.INSUFFICIENT_FUNDS, "Insufficient balance for transfer", List.of(transfer));
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        result[0] = new RequestResponse<>(ResponseStatusCode.INTERNAL_SERVER_ERROR, "Transfer failed due to interruption", List.of(transfer));
                    }

                    System.out.println("Transfer failed: Insufficient balance");
                    return;
                }

                try {
                    blockchainService.lockBlockchain(blockchainService.getBlockchain().getResponseValue(),AccessType.WRITE,()->{
                        // Create debit and credit operations
                        Debit debit = new Debit(sender, amount, "Transfer to " + receiver.getAccountNumber()+" -> " + transfer.getDescription());
                        Credit credit = new Credit(receiver, amount, "Transfer from " + sender.getAccountNumber()+" -> " + transfer.getDescription());

                        // Update the status of the transfer, debit, and credit operations
                        transfer.setStatus(OperationStatus.SUCCESS);
                        debit.setStatus(OperationStatus.SUCCESS);
                        credit.setStatus(OperationStatus.SUCCESS);
                        // Record the operation in the blockchain
                        // In a real implementation, you would likely want to ensure that all operations are recorded atomically to maintain consistency. This might involve using transactions or other mechanisms to ensure that either all operations are recorded successfully or none are.
                        // Record the transfer operation first, followed by the debit and credit operations. This way, if there is an issue with recording the debit or credit, you can handle it appropriately (e.g., by rolling back the transfer).
                        blockchainService.recordOperation(transfer);
                        blockchainService.recordOperation(debit);
                        blockchainService.recordOperation(credit);

                        result[0] = new RequestResponse<>(ResponseStatusCode.SUCCESS, "Transfer completed successfully", Arrays.asList(transfer, debit, credit));
                        System.out.println("Transfer successful: " + amount + " from " + sender.getAccountNumber() + " to " + receiver.getAccountNumber());

                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    result[0] = new RequestResponse<>(ResponseStatusCode.INTERNAL_SERVER_ERROR, "Transfer failed due to interruption", List.of(transfer));
                }


            });
            return result[0];
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new RequestResponse<>(ResponseStatusCode.INTERNAL_SERVER_ERROR, "Transfer failed due to interruption", List.of(transfer));
        }
     }


}
