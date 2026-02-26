package com.m2i.model.transaction;

import com.m2i.model.account.Account;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public sealed abstract class Operation extends BaseTransactionModel permits Debit, Credit, Transfer ,Refund{

    public static final String DESCRIPTION = "description";
    public static final String STATUS = "status";
    public static final String CLIENT = "client";
    public static final String AMOUNT = "amount";
    public static final String TYPE = "type";
    public static final String RECEIVER = "receiver";
    private final String operationId;
    private final BigDecimal amount;
    private final Account account;

    private final TransactionType type;
    private TransactionStatus status;
    private String description;
    private final LocalDateTime transactionDate;


     public Operation(Account account, BigDecimal amount, TransactionType type, String description) {

        checkAttribute(CLIENT, account);
        checkAttribute(AMOUNT, amount);
        checkAttribute(TYPE, type);

        this.account=account;
        this.operationId = UUID.randomUUID().toString();
        this.amount = amount;
        this.type = type;
        this.status = TransactionStatus.PENDING;
        transactionDate=LocalDateTime.now();
        this.description = description;

    }


    public Account getAccount() {
        return account;
    }

    public String getOperationId() {
        return operationId;
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

        public void displayOperationDetails() {
            System.out.println();
            System.out.println("----------------------------------------------------------");
            System.out.println("Operation ID: " + operationId);
            System.out.println("Client: " + account.getClient().getClientName());
            System.out.println("Amount: " + amount);
            System.out.println("Type: " + type);
            System.out.println("Status: " + status);
            System.out.println("Description: " + description);
            System.out.println("Date: " + transactionDate);
        }

        @Override
        public String toString() {
            return "Operation{" +
                    "operationId='" + operationId + '\'' +
                    ", amount=" + amount +
                    ", account=" + account +
                    ", type=" + type +
                    ", status=" + status +
                    ", description='" + description + '\'' +
                    ", transactionDate=" + transactionDate +
                    '}';
        }


}





