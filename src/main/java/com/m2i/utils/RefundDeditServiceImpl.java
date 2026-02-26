package com.m2i.utils;

import com.m2i.model.account.Account;
import com.m2i.model.transaction.*;
import com.m2i.service.RefundService;

import java.math.BigDecimal;
import java.util.List;

public class RefundCreditServiceImpl implements RefundService {


    private BlockchainServiceImpl blockchainService;
    private AccountValidationServiceImpl validationService;
    public RefundCreditServiceImpl (BlockchainServiceImpl blockchainService, AccountValidationServiceImpl validationService) {
        this.blockchainService = blockchainService;
        this.validationService= validationService;

    }


    @Override
    public void doOperation(Refund refund, List<Operation> blockchainBlocks) {

        if(! (refund instanceof RefundCredit rc)){
            throw new IllegalArgumentException("Refund must be of type RefundCredit");
        }

        // Validation de l'opération de remboursement
        Account account= refund.getAccount();
        if(!validationService.canOperate(account)){
            rc.setStatus(TransactionStatus.FAILED);
            blockchainService.recordOperation(rc);
        }
        BigDecimal balance = blockchainService.computeBalance(account);
        if (!validationService.hasSufficientBalance(balance, rc.getAmount())) {
            rc.setStatus(TransactionStatus.FAILED);
            blockchainService.recordOperation(rc);
            return;
        }

            // Effectuer le remboursement
        Debit debit = new Debit(account, rc.getAmount(), "Refund Credit (" + rc.getDescription() + ")");
        debit.setStatus(TransactionStatus.SUCCESS);
        // Enregistrer les opérations dans la blockchain
        blockchainService.recordOperation(rc);
        blockchainService.recordOperation(debit);

    }
}
