package com.example.bakkerij.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class ProductTest {

    @Test
    void shouldCreateProductWithAllFields() {
        Product product = new Product(1, "Brood", "Pain", "Bread", "Brot", "Pan", "面包",
                "Lekker brood", "Bon pain", "Good bread", "Gutes Brot", "Buen pan", "好面包",
                2.50, "bread.jpg", "brood");

        assertThat(product.getId()).isEqualTo(1);
        assertThat(product.getNameNl()).isEqualTo("Brood");
        assertThat(product.getPrice()).isEqualTo(2.50);
        assertThat(product.getCategory()).isEqualTo("brood");
    }

    @Test
    void shouldReturnCorrectNameForLanguage() {
        Product product = new Product(1, "Brood", "Pain", "Bread", "Brot", "Pan", "面包",
                "Desc NL", "Desc FR", "Desc EN", "Desc DE", "Desc ES", "Desc ZH",
                2.50, "bread.jpg", "brood");

        assertThat(product.getNameForLanguage("nl")).isEqualTo("Brood");
        assertThat(product.getNameForLanguage("fr")).isEqualTo("Pain");
        assertThat(product.getNameForLanguage("en")).isEqualTo("Bread");
        assertThat(product.getNameForLanguage("de")).isEqualTo("Brot");
        assertThat(product.getNameForLanguage("es")).isEqualTo("Pan");
        assertThat(product.getNameForLanguage("zh")).isEqualTo("面包");
        assertThat(product.getNameForLanguage("unknown")).isEqualTo("Brood"); // defaults to NL
    }

    @Test
    void shouldReturnCorrectDescriptionForLanguage() {
        Product product = new Product(1, "Brood", "Pain", "Bread", "Brot", "Pan", "面包",
                "Desc NL", "Desc FR", "Desc EN", "Desc DE", "Desc ES", "Desc ZH",
                2.50, "bread.jpg", "brood");

        assertThat(product.getDescriptionForLanguage("nl")).isEqualTo("Desc NL");
        assertThat(product.getDescriptionForLanguage("fr")).isEqualTo("Desc FR");
        assertThat(product.getDescriptionForLanguage("en")).isEqualTo("Desc EN");
    }

    @Test
    void shouldBeEqualWhenSameId() {
        Product product1 = new Product(1, "Brood", "Pain", "Bread", "Brot", "Pan", "面包",
                "Desc", "Desc", "Desc", "Desc", "Desc", "Desc",
                2.50, "bread.jpg", "brood");
        Product product2 = new Product(1, "Different", "Different", "Different", "Different", "Different", "Different",
                "Desc", "Desc", "Desc", "Desc", "Desc", "Desc",
                5.00, "other.jpg", "other");

        assertThat(product1).isEqualTo(product2);
        assertThat(product1.hashCode()).isEqualTo(product2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentId() {
        Product product1 = new Product(1, "Brood", "Pain", "Bread", "Brot", "Pan", "面包",
                "Desc", "Desc", "Desc", "Desc", "Desc", "Desc",
                2.50, "bread.jpg", "brood");
        Product product2 = new Product(2, "Brood", "Pain", "Bread", "Brot", "Pan", "面包",
                "Desc", "Desc", "Desc", "Desc", "Desc", "Desc",
                2.50, "bread.jpg", "brood");

        assertThat(product1).isNotEqualTo(product2);
    }

    @Test
    void shouldHaveValidToString() {
        Product product = new Product(1, "Brood", "Pain", "Bread", "Brot", "Pan", "面包",
                "Desc", "Desc", "Desc", "Desc", "Desc", "Desc",
                2.50, "bread.jpg", "brood");

        String toString = product.toString();
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("nameNl='Brood'");
        assertThat(toString).contains("price=2.5");
        assertThat(toString).contains("category='brood'");
    }
}
