package com.m2i.model.client;

import com.m2i.model.account.Account;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public sealed abstract class Client extends BaseClientModel permits Customer, Company {

    public static final String CLIENT_NAME = "clientName";
    public static final String ACCOUNT = "account";
    public static final String CLIENT_STATUS = "clientStatus";

    protected String phoneNumber; //c
    protected Set <String> othersPhoneNumbers ; //c
    protected String email;
    protected Address address; //enc
    protected  Long clientId ;
    protected String clientName;
    protected Set<Account> accounts;
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String EMAIL = "email";
    public static final String ADDRESS = "address";

    protected ClientStatus clientStatus= ClientStatus.INACTIVE;
    protected LocalDate registrationDate;

    public Client(  String clientName, String phoneNumber, String email, Address address) {
            setPhoneNumber(phoneNumber);
            setEmail(email);
            setAddress(address);
            setClientName(clientName);
        clientId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        registrationDate = LocalDate.now();


    }
    public Client(Long clientId){
        this.clientId = clientId;
        registrationDate= LocalDate.now();

    }
    public Client(String phoneNumber, String clientName, String email, Address address, HashSet<String> othersPhoneNumbers) {
        this( clientName, phoneNumber, email, address);
        setOthersPhoneNumbers(othersPhoneNumbers);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        checkPhoneAttribute(PHONE_NUMBER, phoneNumber);
        this.phoneNumber = phoneNumber;
    }

    public Set<String> getOthersPhoneNumbers() {
        return othersPhoneNumbers;
    }

    public void setOthersPhoneNumbers(HashSet<String> othersPhoneNumbers) {
        this.othersPhoneNumbers = othersPhoneNumbers;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        checkEmailAttribute(EMAIL, email);
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        checkAttribute(ADDRESS, address);
        this.address = address;
    }

    public void  addPhoneNumber(String phoneNumber){
        checkPhoneAttribute(PHONE_NUMBER, phoneNumber);
        if(this.othersPhoneNumbers == null){
            this.othersPhoneNumbers = new HashSet<>();
        }
        this.othersPhoneNumbers.add(phoneNumber);
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientName(String clientName) {
        checkAttribute(CLIENT_NAME, clientName);
        this.clientName = clientName;
    }
    public void setClientStatus(ClientStatus clientStatus) {
        checkAttribute(CLIENT_STATUS, clientStatus);
        this.clientStatus = clientStatus;
    }
    public ClientStatus getClientStatus() {
        return clientStatus;
    }

    public String getClientName() {
        return clientName;
    }

    public String toString() {
         return "Client{" +
                 "phoneNumber='" + phoneNumber + '\'' +
                 ", othersPhoneNumbers=" + othersPhoneNumbers +
                 ", email='" + email + '\'' +
                 ", address=" + address +
                 ", clientId=" + clientId +
                 ", clientName='" + clientName + '\'' +
                 ", registrationDate=" + registrationDate +
                 '}';
     }

    public Set<Account> getAccounts() {
        return accounts;
    }
    public void addAccount(Account account){
        checkAttribute(ACCOUNT, account);
        if(this.accounts == null){
            this.accounts = new HashSet<>();
        }
        this.accounts.add(account);
    }

    public void setAccounts(Set<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public boolean equals(Object o) {

        return hashCode()==o.hashCode();
    }
    @Override
    public int hashCode() {
        int result = phoneNumber.hashCode();
        result = 31 * result + (othersPhoneNumbers != null ? othersPhoneNumbers.hashCode() : 0);
        result = 31 * result + email.hashCode();
        result = 31 * result + address.hashCode();
        result = 31 * result + clientId.hashCode();
        result = 31 * result + clientName.hashCode();
        result = 31 * result + registrationDate.hashCode();
        return result;
    }
}
