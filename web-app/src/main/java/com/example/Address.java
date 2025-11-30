package com.example;

public class Address {
    public String street;
    public String postal;
    public String city;
    public String country;

    public Address(String street, String postal, String city, String country) {
        this.street = street;
        this.postal = postal;
        this.city = city;
        this.country = country;
    }

    public String getStreet() { return street; }
    public String getPostal() { return postal; }
    public String getCity() { return city; }
    public String getCountry() { return country; }
}
