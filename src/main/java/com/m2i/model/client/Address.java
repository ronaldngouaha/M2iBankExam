package com.m2i.model.client;

public final class Address extends BaseClientModel {

    private String streetName;
    private String postalCode;
    private City city;
    private Integer    civicNumber ;
    private  Integer appartNumber;
    private Country country = DEFAULT_COUNTRY ;

    public static final Country DEFAULT_COUNTRY = Country.CANADA ;
    public static final String STREET_NAME = "streetName";
    public static final String POSTAL_CODE = "postalCode";
    public static final String CITY = "city";
    public static final String COUNTRY = "country";
    public static final String CIVIC_NUMBER = "civicNumber";

    public Address(String streetName, String postalCode, City city, Country country, Integer civicNumber, Integer appartNum){
        //appel du constructeur de la classe
        this(streetName, postalCode, city, country, civicNumber);
        appartNumber= appartNum;
    }
    public Address (String streetName, String postalCode, City city, Country country, Integer civicNumber){
         setCity(city);
         setCountry(country);
         setCivicNumber(civicNumber);
         setPostalCode(postalCode);
         setStreetName(streetName);
    }

    public String getStreetName() {
        return streetName;
    }
    public String getPostalCode(){
         return postalCode;
    }
    public City getCity(){
        return city;
    }
    public Country  getCountry(){
        return country;
    }
    public Integer  getCivicNumber(){
        return civicNumber;
    }
    public Integer getAppartNumber(){
        return appartNumber;
    }

    public void setAppartNumber(Integer appartNumber) {
        this.appartNumber = appartNumber;
    }

    public void setCivicNumber(Integer civicNumber) {
        checkAttribute( CIVIC_NUMBER, civicNumber);
        this.civicNumber = civicNumber;
    }

    public void setStreetName(String streetName) {
        checkAttribute(STREET_NAME, streetName);
        this.streetName = streetName;
    }

    public void setPostalCode(String postalCode) {
        checkAttribute(POSTAL_CODE, postalCode);
        this.postalCode = postalCode;
    }

    public void setCity (City city) {
        checkAttribute(CITY, city);
        this.city = city;
    }

    public void setCountry(Country country) {
        checkAttribute(COUNTRY, country);
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {

        Address address = (Address) o;
        if (!streetName.equals(address.streetName)) return false;
        if (!postalCode.equals(address.postalCode)) return false;
        if (city != address.city) return false;
        if (country != address.country) return false;
        if (!civicNumber.equals(address.civicNumber)) return false;
        if(appartNumber != null ? !appartNumber.equals(address.appartNumber) : address.appartNumber != null) return false;
        return super.equals(o);
    }
}


