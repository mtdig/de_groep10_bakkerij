package com.example.bakkerij.handler;

import com.example.bakkerij.model.Product;
import com.example.bakkerij.service.CartService;
import com.example.bakkerij.service.ProductService;
import com.example.bakkerij.util.SessionManager;
import com.example.bakkerij.util.TemplateRenderer;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductHandler {
    private final ProductService productService;
    private final CartService cartService;
    private final TemplateRenderer templateRenderer;
    private final SessionManager sessionManager;

    public ProductHandler(ProductService productService, CartService cartService, 
                         TemplateRenderer templateRenderer, SessionManager sessionManager) {
        this.productService = productService;
        this.cartService = cartService;
        this.templateRenderer = templateRenderer;
        this.sessionManager = sessionManager;
    }

    public void getProducts(Context ctx) {
        String sessionId = sessionManager.getOrCreateSessionId(ctx);
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        
        Map<String, Object> context = new HashMap<>();
        context.put("products", productService.getAllProducts());
        context.put("category", "all");
        context.put("page", "products");
        context.put("cartCount", cartService.getCartCount(sessionId));
        
        String htmxRequest = ctx.header("HX-Request");
        String template = (htmxRequest != null && htmxRequest.equals("true")) 
            ? "templates/products-content.jinja" 
            : "templates/products.jinja";
        
        ctx.html(templateRenderer.render(template, context, lang));
    }

    public void getProductsByCategory(Context ctx) {
        String category = ctx.pathParam("category");
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        
        List<Product> filtered = productService.getProductsByCategory(category);
        
        Map<String, Object> context = new HashMap<>();
        context.put("products", filtered);
        context.put("category", category);
        
        ctx.html(templateRenderer.render("templates/products-area.jinja", context, lang));
    }

    public void getProductDetails(Context ctx) {
        String productId = ctx.pathParam("id");
        String productJson = productService.getProductDetailsJson(productId);
        
        if (productJson == null) {
            ctx.status(404).result("Product not found");
            return;
        }
        
        ctx.contentType("application/json").result(productJson);
    }
}
