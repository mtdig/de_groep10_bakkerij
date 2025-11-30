package com.example.bakkerij.handler;

import com.example.bakkerij.service.CartService;
import com.example.bakkerij.service.ProductService;
import com.example.bakkerij.util.SessionManager;
import com.example.bakkerij.util.TemplateRenderer;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartHandler {
    private final CartService cartService;
    private final ProductService productService;
    private final TemplateRenderer templateRenderer;
    private final SessionManager sessionManager;

    public CartHandler(CartService cartService, ProductService productService, 
                      TemplateRenderer templateRenderer, SessionManager sessionManager) {
        this.cartService = cartService;
        this.productService = productService;
        this.templateRenderer = templateRenderer;
        this.sessionManager = sessionManager;
    }

    public void getCart(Context ctx) {
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        String sessionId = sessionManager.getOrCreateSessionId(ctx);
        
        List<Map<String, Object>> cartItems = cartService.getCartItems(sessionId);
        double total = cartService.getCartTotal(sessionId);
        int itemCount = cartService.getCartCount(sessionId);
        
        Map<String, Object> context = new HashMap<>();
        context.put("cartItems", cartItems);
        context.put("total", total);
        context.put("itemCount", itemCount);
        context.put("page", "cart");
        context.put("cartCount", itemCount);
        
        String htmxRequest = ctx.header("HX-Request");
        String template = (htmxRequest != null && htmxRequest.equals("true")) 
            ? "templates/cart-content.jinja" 
            : "templates/cart.jinja";
        
        ctx.html(templateRenderer.render(template, context, lang));
    }

    public void addToCart(Context ctx) {
        String sessionId = sessionManager.getOrCreateSessionId(ctx);
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        int productId = Integer.parseInt(ctx.pathParam("id"));
        int quantity = ctx.formParam("quantity") != null ? Integer.parseInt(ctx.formParam("quantity")) : 1;
        
        cartService.addToCart(sessionId, productId, quantity);
        
        productService.getProductById(productId).ifPresentOrElse(
            product -> {
                String productName = product.getNameForLanguage(lang);
                String escapedName = productName.replace("\"", "\\\"")
                                                .replace("\n", "\\n")
                                                .replace("\r", "\\r");
                
                ctx.header("HX-Trigger", "{\"showCartModal\":{\"lang\":\"" + lang + 
                                         "\",\"type\":\"add\",\"productName\":\"" + escapedName + 
                                         "\",\"quantity\":" + quantity + ",\"price\":" + product.getPrice() + "}}");
            },
            () -> ctx.header("HX-Trigger", "{\"showCartModal\":{\"lang\":\"" + lang + "\",\"type\":\"add\"}}")
        );
        
        ctx.html(getCartCountHtml(sessionId));
    }

    public void updateCartQuantity(Context ctx) {
        String sessionId = sessionManager.getOrCreateSessionId(ctx);
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        int productId = Integer.parseInt(ctx.pathParam("id"));
        int quantity = Integer.parseInt(ctx.formParam("quantity"));
        
        cartService.updateCartItem(sessionId, productId, quantity);
        renderCartContent(ctx, lang);
    }

    public void removeFromCart(Context ctx) {
        String sessionId = sessionManager.getOrCreateSessionId(ctx);
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        int productId = Integer.parseInt(ctx.pathParam("id"));
        
        cartService.removeFromCart(sessionId, productId);
        renderCartContent(ctx, lang);
    }

    public void getCartCount(Context ctx) {
        String sessionId = sessionManager.getOrCreateSessionId(ctx);
        ctx.html(getCartCountHtml(sessionId));
    }

    private void renderCartContent(Context ctx, String lang) {
        String sessionId = sessionManager.getOrCreateSessionId(ctx);
        List<Map<String, Object>> cartItems = cartService.getCartItems(sessionId);
        double total = cartService.getCartTotal(sessionId);
        int itemCount = cartService.getCartCount(sessionId);
        
        Map<String, Object> context = new HashMap<>();
        context.put("cartItems", cartItems);
        context.put("total", total);
        context.put("itemCount", itemCount);
        
        ctx.html(templateRenderer.render("templates/cart-content.jinja", context, lang));
    }

    private String getCartCountHtml(String sessionId) {
        int count = cartService.getCartCount(sessionId);
        return count > 0 ? "<span class=\"cart-count\">" + count + "</span>" : "";
    }
}
