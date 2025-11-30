package com.example.bakkerij.model;

import java.util.Objects;

public class Address {
    private final String street;
    private final String postal;
    private final String city;
    private final String country;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street) &&
               Objects.equals(postal, address.postal) &&
               Objects.equals(city, address.city) &&
               Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, postal, city, country);
    }

    @Override
    public String toString() {
        return "Address{street='" + street + "', postal='" + postal + "', city='" + city + "', country='" + country + "'}";
    }
}
