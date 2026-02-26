package com.m2i.model.transaction;

import com.m2i.model.account.Account;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public final class Transaction  extends BaseTransactionModel{
    public static final String ONLY_PENDING_TRANSACTIONS_CAN_BE_CANCELLED = "Only pending transactions can be cancelled";
    public static final String DESCRIPTION = "description";
    public static final String STATUS = "status";
    public static final String CLIENT = "client";
    public static final String AMOUNT = "amount";
    public static final String TYPE = "type";
    public static final String RECEIVER = "receiver";
    private final String transactionId;
    private final BigDecimal amount;
    private final Account client;
    private Account receiver;
    private final TransactionType type;
    private TransactionStatus status;
    private String description;
    private final LocalDateTime transactionDate;


    public Transaction(Account client, BigDecimal amount, TransactionType type, String description) {

       checkAttribute(CLIENT, client);
       checkAttribute(AMOUNT, amount);
       checkAttribute(TYPE, type);

        this.client=client;
        this.transactionId = UUID.randomUUID().toString();
        this.amount = amount;
        this.type = type;
        this.status = TransactionStatus.PENDING;
        transactionDate=LocalDateTime.now();
        this.description = description;

    }

    public Transaction(Account sender, Account receiver, BigDecimal amount, TransactionType type, String description) {
        this(sender, amount, type, description);
        checkAttribute(RECEIVER, receiver);
        this.receiver=receiver;
    }


    public String getTransactionId() {
        return transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        checkAttribute(STATUS, status);
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        checkAttribute(DESCRIPTION, description);
        this.description = description;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
        public Account getClient() {
            return client;
        }

    public Account getReceiver() {
        return receiver;
    }

    // Permet de marquer une transaction comme "COMPLETED", en vérifiant que son statut est actuellement "PENDING" avant de le faire.
    public void completeTransaction() {
        if (status != TransactionStatus.PENDING) {
            throw new IllegalStateException(ONLY_PENDING_TRANSACTIONS_CAN_BE_CANCELLED  );
        }
        setStatus(TransactionStatus.COMPLETED);
    }
    // Permet d'annuler une transaction en cours, en vérifiant que son statut est "PENDING" ou "COMPLETED" avant de la marquer comme "CANCELLED".
    public void cancelTransaction() {
        if (status != TransactionStatus.PENDING && status != TransactionStatus.COMPLETED) {
            throw new IllegalStateException(ONLY_PENDING_TRANSACTIONS_CAN_BE_CANCELLED + ".");
        }
        setStatus(TransactionStatus.CANCELLED);
    }

}



 enum TransactionStatus {
    PENDING,
    COMPLETED,
    CANCELLED
}

 enum TransactionType {
    CREDIT,
    DEBIT,
    TRANSFER,
    PAYMENT,
    WITHDRAWAL,
    DEPOSIT,
    REFUND,
    REVERSAL
}