package com.example.bakkerij.handler;

import com.example.bakkerij.model.Address;
import com.example.bakkerij.model.Order;
import com.example.bakkerij.service.CartService;
import com.example.bakkerij.service.OrderService;
import com.example.bakkerij.service.UserService;
import com.example.bakkerij.util.SessionManager;
import com.example.bakkerij.util.TemplateRenderer;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountHandler {
    private final UserService userService;
    private final OrderService orderService;
    private final CartService cartService;
    private final TemplateRenderer templateRenderer;
    private final SessionManager sessionManager;

    public AccountHandler(UserService userService, OrderService orderService, CartService cartService,
                         TemplateRenderer templateRenderer, SessionManager sessionManager) {
        this.userService = userService;
        this.orderService = orderService;
        this.cartService = cartService;
        this.templateRenderer = templateRenderer;
        this.sessionManager = sessionManager;
    }

    public void getAccount(Context ctx) {
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        String sessionId = sessionManager.getOrCreateSessionId(ctx);
        
        Map<String, Object> context = new HashMap<>();
        context.put("page", "account");
        context.put("cartCount", cartService.getCartCount(sessionId));
        
        userService.getUsername(sessionId).ifPresent(username -> {
            List<Order> orders = orderService.getOrderHistory(username);
            Address address = userService.getAddress(username).orElse(null);
            context.put("username", username);
            context.put("orders", orders);
            context.put("address", address);
        });
        
        String htmxRequest = ctx.header("HX-Request");
        String template = (htmxRequest != null && htmxRequest.equals("true")) 
            ? "templates/account-content.jinja" 
            : "templates/account.jinja";
        
        ctx.html(templateRenderer.render(template, context, lang));
    }

    public void login(Context ctx) {
        String firstname = ctx.formParam("firstname");
        String password = ctx.formParam("password");
        String lang = ctx.formParam("lang");
        String sessionId = sessionManager.getOrCreateSessionId(ctx);
        
        if (userService.login(sessionId, firstname, password)) {
            // Generate order history if not exists
            orderService.generateOrderHistory(firstname);
            ctx.redirect("/account?lang=" + lang);
        } else {
            ctx.redirect("/account?lang=" + lang + "&error=invalid");
        }
    }

    public void logout(Context ctx) {
        String sessionId = sessionManager.getOrCreateSessionId(ctx);
        userService.logout(sessionId);
        ctx.redirect("/account?lang=" + ctx.formParam("lang"));
    }

    public void changePassword(Context ctx) {
        String sessionId = sessionManager.getOrCreateSessionId(ctx);
        String lang = ctx.formParam("lang") != null ? ctx.formParam("lang") : "nl";
        
        if (!userService.isLoggedIn(sessionId)) {
            ctx.status(401).html("<div class=\"error-message\">" + 
                (lang.equals("nl") ? "Niet ingelogd" : "Non connecté") + "</div>");
            return;
        }
        
        String newPassword = ctx.formParam("newPassword");
        String confirmPassword = ctx.formParam("confirmPassword");
        
        if (!newPassword.equals(confirmPassword)) {
            ctx.html("<div class=\"error-message\">" + 
                (lang.equals("nl") ? "Wachtwoorden komen niet overeen" : "Les mots de passe ne correspondent pas") + "</div>");
            return;
        }
        
        ctx.html("<div class=\"success-message\">" + 
            (lang.equals("nl") ? "Wachtwoord succesvol gewijzigd!" : "Mot de passe modifié avec succès!") + "</div>");
    }

    public void updateAddress(Context ctx) {
        String sessionId = sessionManager.getOrCreateSessionId(ctx);
        String lang = ctx.formParam("lang") != null ? ctx.formParam("lang") : "nl";
        
        userService.getUsername(sessionId).ifPresentOrElse(
            username -> {
                String street = ctx.formParam("street");
                String postal = ctx.formParam("postal");
                String city = ctx.formParam("city");
                String country = ctx.formParam("country");
                
                Address address = new Address(street, postal, city, country);
                userService.updateAddress(username, address);
                
                ctx.html("<div class=\"success-message\">" + 
                    (lang.equals("nl") ? "Adres succesvol opgeslagen!" : "Adresse enregistrée avec succès!") + "</div>");
            },
            () -> ctx.status(401).html("<div class=\"error-message\">" + 
                (lang.equals("nl") ? "Niet ingelogd" : "Non connecté") + "</div>")
        );
    }

    public void repeatOrder(Context ctx) {
        String sessionId = sessionManager.getOrCreateSessionId(ctx);
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        
        userService.getUsername(sessionId).ifPresentOrElse(
            username -> {
                int orderNumber = Integer.parseInt(ctx.pathParam("orderNumber"));
                orderService.findOrderByNumber(username, orderNumber).ifPresentOrElse(
                    order -> {
                        order.getItems().forEach(item -> 
                            cartService.addToCart(sessionId, item.getProduct().getId(), item.getQuantity())
                        );
                        ctx.header("HX-Trigger", "{\"showCartModal\":{\"lang\":\"" + lang + "\",\"type\":\"repeat\"}}");
                        ctx.html(getCartCountHtml(sessionId));
                    },
                    () -> ctx.status(404).result("Order not found")
                );
            },
            () -> ctx.status(401).result("Not logged in")
        );
    }

    private String getCartCountHtml(String sessionId) {
        int count = cartService.getCartCount(sessionId);
        return count > 0 ? "<span class=\"cart-count\">" + count + "</span>" : "";
    }
}
