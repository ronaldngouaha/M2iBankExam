package com.m2i.model.client;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.UUID;

public final class Company extends Client {

    public static final String BUSINESS_NUMBER = "businessNumber";
    public static final String TAX_NUMBER = "taxNumber";
    public static final String WEBSITE = "website";
    public static final String CONTACT_PERSON = "contactPerson";
    public static final String INDUSTRY = "industry";
    public static final String COMPANY_SIZE = "companySize";
    public static final String ANNUAL_REVENUE = "annualRevenue";

    private  String businessNumber;
    private String taxNumber;
    private String website;
    private String contactPerson;
    private Industry industry;
    private Integer companySize;
    private BigDecimal annualRevenue;

    public Company(String companyName, String businessNumber, String taxNumber, String phoneNumber, String email, Address address) {
        super(companyName, phoneNumber, email, address);
        setBusinessNumber(businessNumber);
        setTaxNumber(taxNumber);

    }

    public Company( String companyName,  String businessNumber, String taxNumber, String phoneNumber , HashSet<String> othersPhoneNumbers, String email, Address address, String website, String contactPerson, Industry industry, Integer companySize, BigDecimal annualRevenue) {
       this(companyName, businessNumber, taxNumber, phoneNumber, email, address);
       setBusinessNumber(businessNumber);
       setTaxNumber(taxNumber);
       setWebsite(website);
       setContactPerson(contactPerson);
       setIndustry(industry);
       setCompanySize(companySize);
       setAnnualRevenue(annualRevenue);
       setOthersPhoneNumbers(othersPhoneNumbers);

    }

    public Company( String companyName,  String businessNumber, String taxNumber, String phoneNumber , HashSet<String> othersPhoneNumbers, String email, Address address) {
      this(companyName, businessNumber, taxNumber, phoneNumber, email, address);
       setOthersPhoneNumbers(othersPhoneNumbers);
    }

    public String getCompanyName() {
        return clientName;
    }


    public String getBusinessNumber() {
        return businessNumber;
    }

    public void setBusinessNumber(String businessNumber) {
        checkAttribute(BUSINESS_NUMBER, businessNumber);
        this.businessNumber = businessNumber;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        checkAttribute(TAX_NUMBER, taxNumber);
        this.taxNumber = taxNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        checkAttribute(WEBSITE, website);
        this.website = website;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        checkAttribute(CONTACT_PERSON, contactPerson);
        this.contactPerson = contactPerson;
    }

    public Industry getIndustry() {
        return industry;
    }

    public void setIndustry(Industry industry) {
        checkAttribute(INDUSTRY, industry);
        this.industry = industry;
    }

    public Integer getCompanySize() {
        return companySize;
    }

    public void setCompanySize(Integer companySize) {
        checkAttribute(COMPANY_SIZE, companySize);
        this.companySize = companySize;
    }

    public BigDecimal getAnnualRevenue() {
        return annualRevenue;
    }

    public void setAnnualRevenue(BigDecimal annualRevenue) {
        checkAttribute(ANNUAL_REVENUE, annualRevenue);
        this.annualRevenue = annualRevenue;
    }

    @Override
    public String toString() {
        return "Company{" +
                "companyName='" + clientName + '\'' +
                ", businessNumber='" + businessNumber + '\'' +
                ", taxNumber='" + taxNumber + '\'' +
                ", website='" + website + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", industry=" + industry +
                ", companySize=" + companySize +
                ", annualRevenue=" + annualRevenue +
                '}';
    }

        @Override
    public boolean equals(Object o) {
        Company company = (Company) o;
        if (!getCompanyName().equals(company.getCompanyName())) return false;
        if (!businessNumber.equals(company.businessNumber)) return false;
        if (!taxNumber.equals(company.taxNumber)) return false;
        if (website != null ? !website.equals(company.website) : company.website != null)            return false;
        if (contactPerson != null ? !contactPerson.equals(company.contactPerson) : company.contactPerson != null) return false;
        if (industry != company.industry) return false;
        if (companySize != null ? !companySize.equals(company.companySize) : company.companySize != null) return false;
        if (annualRevenue != null ? !annualRevenue.equals(company.annualRevenue) : company.annualRevenue != null) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        int result = getCompanyName().hashCode();
        result = 31 * result + businessNumber.hashCode();
        result = 31 * result + taxNumber.hashCode();
        result = 31 * result + (website != null ? website.hashCode() : 0);
        result = 31 * result + (contactPerson != null ? contactPerson.hashCode() : 0);
        result = 31 * result + (industry != null ? industry.hashCode() : 0);
        result = 31 * result + (companySize != null ? companySize.hashCode() : 0);
        result = 31 * result + (annualRevenue != null ? annualRevenue.hashCode() : 0);
        return result;
    }

}

