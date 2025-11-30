package com.example.bakkerij.repository;

import com.example.bakkerij.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProductRepository {
    private final List<Product> products = new ArrayList<>();
    private final ObjectMapper mapper = new ObjectMapper();

    public void loadProducts(String resourceName) {
        try {
            InputStream productsStream = getClass().getClassLoader().getResourceAsStream(resourceName);
            if (productsStream == null) {
                System.err.println("Failed to load " + resourceName + " - file not found");
                return;
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Object>> productData = mapper.readValue(productsStream, Map.class);
            
            for (Map.Entry<String, Map<String, Object>> entry : productData.entrySet()) {
                Map<String, Object> data = entry.getValue();
                Product product = new Product(
                    ((Number) data.get("id")).intValue(),
                    (String) data.get("nameNl"),
                    (String) data.get("nameFr"),
                    (String) data.get("nameEn"),
                    (String) data.get("nameDe"),
                    (String) data.get("nameEs"),
                    (String) data.get("nameZh"),
                    (String) data.get("descriptionNl"),
                    (String) data.get("descriptionFr"),
                    (String) data.get("descriptionEn"),
                    (String) data.get("descriptionDe"),
                    (String) data.get("descriptionEs"),
                    (String) data.get("descriptionZh"),
                    ((Number) data.get("price")).doubleValue(),
                    (String) data.get("image"),
                    (String) data.get("category")
                );
                products.add(product);
            }
            System.out.println("Loaded " + products.size() + " products from " + resourceName);
        } catch (Exception e) {
            System.err.println("Failed to load products: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Product> findAll() {
        return new ArrayList<>(products);
    }

    public Optional<Product> findById(int id) {
        return products.stream()
            .filter(p -> p.getId() == id)
            .findFirst();
    }

    public List<Product> findByCategory(String category) {
        return products.stream()
            .filter(p -> p.getCategory().equals(category))
            .toList();
    }

    public String getProductDetailsJson(String productId) {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("bread_details.json");
            if (is == null) {
                return null;
            }
            
            String jsonContent = new String(is.readAllBytes());
            return extractProductJson(jsonContent, productId);
        } catch (Exception e) {
            return null;
        }
    }

    private String extractProductJson(String jsonContent, String productId) {
        try {
            String searchKey = "\"" + productId + "\":";
            int startIndex = jsonContent.indexOf(searchKey);
            if (startIndex == -1) return null;
            
            startIndex = jsonContent.indexOf("{", startIndex);
            int braceCount = 1;
            int endIndex = startIndex + 1;
            
            while (braceCount > 0 && endIndex < jsonContent.length()) {
                char c = jsonContent.charAt(endIndex);
                if (c == '{') braceCount++;
                else if (c == '}') braceCount--;
                endIndex++;
            }
            
            return jsonContent.substring(startIndex, endIndex);
        } catch (Exception e) {
            return null;
        }
    }
}
