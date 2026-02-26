package com.m2i.model.client;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.UUID;

public  non-sealed class Customer extends Client  {

    private static final int PREMIIUM_THESHOLD = 1000;
    private boolean isPremium;
    private Integer loyaltyPoints;
    private String firstName;

    private static  final String LAST_NAME = "lastName";
    private static final String FIRST_NAME = "firstName";


    public Customer(String clientName, String firstName, String phoneNumber, String email, Address address) {
        super(clientName, phoneNumber, email, address);
        setFirstName(firstName);
        loyaltyPoints = 0;
        isPremium = false;
    }

    public Customer(String clientName, String firstName, String phoneNumber, HashSet<String> othersPhoneNumbers,
            String email, Address address) {

        this(clientName, firstName, phoneNumber, email, address);
        setOthersPhoneNumbers(othersPhoneNumbers);

    }



    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        checkAttribute(FIRST_NAME, firstName);
        this.firstName = firstName;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

        public void addLoyaltyPoints(Integer points){
             checkAttribute("Points", points);
            setLoyaltyPoints(getLoyaltyPoints() + points);
            if(getLoyaltyPoints() >= PREMIIUM_THESHOLD){
                setPremium(true);
            }
        }


        @Override
        public boolean equals(Object o) {

            return hashCode()==o.hashCode();
        }

        @Override
    public int hashCode() {
        int result = getClientName().hashCode();
        result = 31 * result + getPhoneNumber().hashCode();
        result = 31 * result + firstName.hashCode();
        result = 31 * result + (isPremium ? 1 : 0);
        result = 31 * result + loyaltyPoints.hashCode();
        return result;
    }


}
