package com.m2i.model.account;

import com.m2i.model.client.Client;
import com.m2i.model.transaction.RefundCredit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public final class Account extends BaseAccountModel {

    private   final Long accountId;
    protected final String accountNumber;

    private final Client client;
    private final LocalDateTime creationDate;
    private final AccountType accountType;
    protected AccountStatus accountStatus;
    private final ReentrantLock lock= new ReentrantLock();
    private static final AtomicLong    counter = new AtomicLong(100000);


    public Account(Client client, AccountType accountType) {
        // Validation
        checkAttribute("client", client);
        checkAttribute("accountType", accountType);
        this.accountType = accountType;
        this.client = client;
        this.accountId = java.util.UUID.randomUUID().getLeastSignificantBits();
        this.accountNumber = generateAccountNumber();
        this.creationDate = LocalDateTime.now();
        this.accountStatus=AccountStatus.INACTIVE;

    }

    public ReentrantLock getLock() {
        return lock;
    }

    public Account(Client client, AccountType accountType, AccountStatus accountStatus) {
        // Validation
        checkAttribute("client", client);
        checkAttribute("accountType", accountType);
        this.accountType = accountType;
        this.client = client;
        this.accountId = java.util.UUID.randomUUID().getLeastSignificantBits();
        this.accountNumber = generateAccountNumber();
        this.creationDate = LocalDateTime.now();
        this.accountStatus = accountStatus;

    }

    public Account(Long accountId){
        this.accountId = accountId;
        this.accountNumber = generateAccountNumber();

        this.client = null;
        this.creationDate = LocalDateTime.now();
        this.accountType = null;
    }

    public String generateAccountNumber(){
       long number = counter.getAndIncrement();
       return String.format("ACC-%06d", number);
    }
    public Long getAccountId() {
        return accountId;
    }
    public Client getClient() {
        return client;
    }
    public String getAccountNumber() {
        return accountNumber;
    }
    public LocalDateTime getCreationDate() {
        return creationDate;
    }
    public AccountType getAccountType() {
        return accountType;
    }

    @Override
    public boolean equals(Object o) {

        return hashCode()==o.hashCode();
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        checkAttribute("accountStatus", accountStatus);
        this.accountStatus = accountStatus;
    }

    @Override
    public int hashCode() {
        int result = accountId.hashCode();
        result = 31 * result + (accountNumber != null ? accountNumber.hashCode() : 0);
        result = 31 * result + (client != null ? client.hashCode() : 0);
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 31 * result + (accountType != null ? accountType.hashCode() : 0);
        return result;
    }


}
