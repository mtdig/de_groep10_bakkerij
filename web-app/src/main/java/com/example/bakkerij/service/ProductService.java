package com.example.bakkerij.service;

import com.example.bakkerij.model.Product;
import com.example.bakkerij.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(int id) {
        return productRepository.findById(id);
    }

    public List<Product> getProductsByCategory(String category) {
        if ("all".equals(category)) {
            return productRepository.findAll();
        }
        return productRepository.findByCategory(category);
    }

    public String getProductDetailsJson(String productId) {
        return productRepository.getProductDetailsJson(productId);
    }
}
