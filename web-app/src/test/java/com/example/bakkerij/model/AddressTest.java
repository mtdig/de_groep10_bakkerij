package com.example.bakkerij.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class AddressTest {

    @Test
    void shouldCreateAddress() {
        Address address = new Address("Main Street 123", "1234AB", "Amsterdam", "Netherlands");

        assertThat(address.getStreet()).isEqualTo("Main Street 123");
        assertThat(address.getPostal()).isEqualTo("1234AB");
        assertThat(address.getCity()).isEqualTo("Amsterdam");
        assertThat(address.getCountry()).isEqualTo("Netherlands");
    }

    @Test
    void shouldBeEqualWhenAllFieldsMatch() {
        Address address1 = new Address("Main Street 123", "1234AB", "Amsterdam", "Netherlands");
        Address address2 = new Address("Main Street 123", "1234AB", "Amsterdam", "Netherlands");

        assertThat(address1).isEqualTo(address2);
        assertThat(address1.hashCode()).isEqualTo(address2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenFieldsDiffer() {
        Address address1 = new Address("Main Street 123", "1234AB", "Amsterdam", "Netherlands");
        Address address2 = new Address("Other Street 456", "1234AB", "Amsterdam", "Netherlands");

        assertThat(address1).isNotEqualTo(address2);
    }

    @Test
    void shouldHaveValidToString() {
        Address address = new Address("Main Street 123", "1234AB", "Amsterdam", "Netherlands");

        String toString = address.toString();
        assertThat(toString).contains("street='Main Street 123'");
        assertThat(toString).contains("postal='1234AB'");
        assertThat(toString).contains("city='Amsterdam'");
        assertThat(toString).contains("country='Netherlands'");
    }
}
