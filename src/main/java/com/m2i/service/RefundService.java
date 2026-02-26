package com.m2i.service;

import com.m2i.model.transaction.Operation;
import com.m2i.model.transaction.Refund;

import java.util.List;

public interface RefundService {

    /**
     * Exécute un remboursement, quel que soit son type concret :
     * RefundCredit, RefundDebit ou RefundTransfer.
     */
    void doOperation(Refund refund);

}
