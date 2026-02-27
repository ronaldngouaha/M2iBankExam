package com.m2i.model.transaction;

import com.m2i.exception.IllegalParameterArgumentException;
import com.m2i.model.account.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public sealed abstract class Operation <T extends Account> extends BaseTransactionModel implements Comparable<Operation> permits FinancialOperation, NonFinancialOperation{

    public static final String DESCRIPTION = "description";
    public static final String STATUS = "status";
    public static final String CLIENT = "client";
    public static final String AMOUNT = "amount";
    public static final String TYPE = "type";
    private final String operationId;
    protected   BigDecimal amount;
    private final T account;
    private final OperationType type;
    private OperationStatus status;
    private String description;
    private final LocalDateTime transactionDate;
    private T receiver;

    private  LocalDateTime statementStartDate;
    private  LocalDateTime statementEndDate;
    private  Integer maxEntry;

    protected Integer numberOfTransactions;

    public Operation(T account, OperationType type, LocalDateTime statementStartDate, LocalDateTime statementEndDate, Integer maxEntry) {

        if(statementEndDate.isBefore(statementStartDate)){
            throw  IllegalParameterArgumentException.forInvalid("statementEndDate", "Statement end date cannot be before start date");
        }
         this(account, type);
        this.statementEndDate = statementEndDate;
        this.statementStartDate = statementStartDate;
        this.maxEntry= maxEntry;
    }


    public LocalDateTime getStatementStartDate() {
        return statementStartDate;
    }

    public LocalDateTime getStatementEndDate() {
        return statementEndDate;
    }

    public Integer getMaxEntry() {
        return maxEntry;
    }

     public Operation(T account, BigDecimal amount, OperationType type, String description) {

        this(account, type, description);
        checkAttribute(AMOUNT, amount);
        this.amount = amount;
        this.description = description;

    }

    public Operation(T account ,OperationType type){
        checkAttribute(CLIENT, account);
        this.account=account;
        this.operationId = UUID.randomUUID().toString();
        transactionDate=LocalDateTime.now();
        this.status = OperationStatus.PENDING;
        checkAttribute(TYPE, type);
        this.type = type;
    }

    public Operation(T sender, T receiver, BigDecimal amount, OperationType type, String description) {
        this(sender, amount, type, description);
        this.receiver = receiver;

    }

    public Operation(T account , OperationType type, String description) {
        this(account, type);
        this.status = OperationStatus.PENDING;
        this.description = description;
    }


    public T getAccount() {

        return account;
    }

    public T getReceiver() {
        return receiver;
    }

    public Integer getNumberOfTransactions() {
        return numberOfTransactions;
    }

    public String getOperationId() {
        return operationId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public OperationType getType() {
        return type;
    }

    public OperationStatus getStatus() {
        return status;
    }

    public void setStatus(OperationStatus status) {
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

        @Override
            public int compareTo(Operation other) {
            return this.getTransactionDate().compareTo(other.getTransactionDate());
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





