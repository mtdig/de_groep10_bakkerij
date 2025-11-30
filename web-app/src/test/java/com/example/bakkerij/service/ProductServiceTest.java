package com.example.bakkerij.service;

import com.example.bakkerij.model.Product;
import com.example.bakkerij.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }

    @Test
    void shouldGetAllProducts() {
        List<Product> products = List.of(
                createTestProduct(1, "Brood", "brood"),
                createTestProduct(2, "Croissant", "gebak")
        );
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        assertThat(result).hasSize(2);
        verify(productRepository).findAll();
    }

    @Test
    void shouldGetProductById() {
        Product product = createTestProduct(1, "Brood", "brood");
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
    }

    @Test
    void shouldReturnEmptyWhenProductNotFound() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById(999);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldGetProductsByCategory() {
        List<Product> broodProducts = List.of(
                createTestProduct(1, "Brood", "brood"),
                createTestProduct(2, "Baguette", "brood")
        );
        when(productRepository.findByCategory("brood")).thenReturn(broodProducts);

        List<Product> result = productService.getProductsByCategory("brood");

        assertThat(result).hasSize(2);
        verify(productRepository).findByCategory("brood");
    }

    @Test
    void shouldGetAllProductsWhenCategoryIsAll() {
        List<Product> allProducts = List.of(
                createTestProduct(1, "Brood", "brood"),
                createTestProduct(2, "Croissant", "gebak")
        );
        when(productRepository.findAll()).thenReturn(allProducts);

        List<Product> result = productService.getProductsByCategory("all");

        assertThat(result).hasSize(2);
        verify(productRepository).findAll();
        verify(productRepository, never()).findByCategory(anyString());
    }

    @Test
    void shouldGetProductDetailsJson() {
        String json = "{\"id\":1,\"name\":\"Brood\"}";
        when(productRepository.getProductDetailsJson("1")).thenReturn(json);

        String result = productService.getProductDetailsJson("1");

        assertThat(result).isEqualTo(json);
        verify(productRepository).getProductDetailsJson("1");
    }

    private Product createTestProduct(int id, String name, String category) {
        return new Product(id, name, name, name, name, name, name,
                "Desc", "Desc", "Desc", "Desc", "Desc", "Desc",
                2.50, "image.jpg", category);
    }
}
