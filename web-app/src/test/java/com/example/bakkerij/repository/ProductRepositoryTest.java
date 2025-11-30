package com.example.bakkerij.repository;

import com.example.bakkerij.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class ProductRepositoryTest {

    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository = new ProductRepository();
        productRepository.loadProducts("bread_details.json");
    }

    @Test
    void shouldLoadProductsFromJson() {
        List<Product> products = productRepository.findAll();

        assertThat(products).isNotEmpty();
        assertThat(products).allMatch(p -> p.getId() > 0);
        assertThat(products).allMatch(p -> p.getNameNl() != null);
        assertThat(products).allMatch(p -> p.getPrice() > 0);
    }

    @Test
    void shouldFindProductById() {
        List<Product> products = productRepository.findAll();
        int firstProductId = products.get(0).getId();

        Optional<Product> result = productRepository.findById(firstProductId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(firstProductId);
    }

    @Test
    void shouldReturnEmptyForNonExistentId() {
        Optional<Product> result = productRepository.findById(999999);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindProductsByCategory() {
        List<Product> allProducts = productRepository.findAll();
        String category = allProducts.get(0).getCategory();

        List<Product> result = productRepository.findByCategory(category);

        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(p -> p.getCategory().equals(category));
    }

    @Test
    void shouldReturnEmptyListForNonExistentCategory() {
        List<Product> result = productRepository.findByCategory("nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldGetProductDetailsJson() {
        List<Product> products = productRepository.findAll();
        int firstProductId = products.get(0).getId();

        String json = productRepository.getProductDetailsJson(String.valueOf(firstProductId));

        assertThat(json).isNotNull();
        assertThat(json).contains("\"id\"");
        assertThat(json).contains("\"nameNl\"");
        assertThat(json).contains("\"price\"");
    }

    @Test
    void shouldReturnNullForInvalidId() {
        String json = productRepository.getProductDetailsJson("999999");

        assertThat(json).isNull();
    }

    @Test
    void shouldHaveUniqueProductIds() {
        List<Product> products = productRepository.findAll();

        long uniqueIds = products.stream()
                .map(Product::getId)
                .distinct()
                .count();

        assertThat(uniqueIds).isEqualTo(products.size());
    }

    @Test
    void shouldHaveValidPrices() {
        List<Product> products = productRepository.findAll();

        assertThat(products).allMatch(p -> p.getPrice() >= 0);
    }
}
