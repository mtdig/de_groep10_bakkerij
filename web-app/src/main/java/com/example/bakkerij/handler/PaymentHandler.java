package com.example.bakkerij.handler;

import com.example.bakkerij.model.OrderItem;
import com.example.bakkerij.service.CartService;
import com.example.bakkerij.service.OrderService;
import com.example.bakkerij.service.UserService;
import com.example.bakkerij.util.SessionManager;
import com.example.bakkerij.util.TemplateRenderer;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentHandler {
    private final UserService userService;
    private final OrderService orderService;
    private final CartService cartService;
    private final TemplateRenderer templateRenderer;
    private final SessionManager sessionManager;

    public PaymentHandler(UserService userService, OrderService orderService, CartService cartService,
                         TemplateRenderer templateRenderer, SessionManager sessionManager) {
        this.userService = userService;
        this.orderService = orderService;
        this.cartService = cartService;
        this.templateRenderer = templateRenderer;
        this.sessionManager = sessionManager;
    }

    public void getPickup(Context ctx) {
        String sessionId = sessionManager.getOrCreateSessionId(ctx);
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        
        if (!userService.isLoggedIn(sessionId)) {
            ctx.redirect("/account?lang=" + lang);
            return;
        }
        
        LocalDate today = LocalDate.now();
        LocalDate minDate = today.plusDays(1);
        LocalDate maxDate = today.plusDays(30);
        
        Map<String, Object> context = new HashMap<>();
        context.put("page", "pickup");
        context.put("minDate", minDate.toString());
        context.put("maxDate", maxDate.toString());
        
        String htmxRequest = ctx.header("HX-Request");
        String template = (htmxRequest != null && htmxRequest.equals("true")) 
            ? "templates/pickup-content.jinja" 
            : "templates/pickup.jinja";
        
        ctx.html(templateRenderer.render(template, context, lang));
    }

    public void confirmPickup(Context ctx) {
        String sessionId = sessionManager.getOrCreateSessionId(ctx);
        String pickupDate = ctx.formParam("pickupDate");
        String pickupTime = ctx.formParam("pickupTime");
        
        userService.savePickupDetails(sessionId, pickupDate, pickupTime);
        getPayment(ctx);
    }

    public void getPayment(Context ctx) {
        String sessionId = sessionManager.getOrCreateSessionId(ctx);
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        String retry = ctx.queryParam("retry");
        
        if (!userService.isLoggedIn(sessionId)) {
            ctx.redirect("/account?lang=" + lang);
            return;
        }
        
        Map<String, Object> context = new HashMap<>();
        context.put("page", "payment");
        context.put("retry", retry != null && retry.equals("true"));
        context.put("lastPaymentMethod", userService.getLastPaymentMethod(sessionId));
        
        String htmxRequest = ctx.header("HX-Request");
        String template = (htmxRequest != null && htmxRequest.equals("true")) 
            ? "templates/payment-content.jinja" 
            : "templates/payment.jinja";
        
        ctx.html(templateRenderer.render(template, context, lang));
    }

    public void getPaymentSuccess(Context ctx) {
        String sessionId = sessionManager.getOrCreateSessionId(ctx);
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        String paymentMethod = ctx.queryParam("method");
        boolean isCashPayment = "cash".equals(paymentMethod);
        
        userService.getUsername(sessionId).ifPresent(username -> {
            if (!cartService.isCartEmpty(sessionId)) {
                List<OrderItem> orderItems = cartService.getOrderItems(sessionId);
                orderService.createOrder(username, orderItems);
                cartService.clearCart(sessionId);
            }
        });
        
        Map<String, Object> context = new HashMap<>();
        context.put("page", "payment");
        context.put("isCashPayment", isCashPayment);
        
        String htmxRequest = ctx.header("HX-Request");
        String template = (htmxRequest != null && htmxRequest.equals("true")) 
            ? "templates/payment-success-content.jinja" 
            : "templates/payment-success.jinja";
        
        ctx.html(templateRenderer.render(template, context, lang));
    }

    public void getPaymentFailed(Context ctx) {
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        
        Map<String, Object> context = new HashMap<>();
        context.put("page", "payment");
        
        String htmxRequest = ctx.header("HX-Request");
        String template = (htmxRequest != null && htmxRequest.equals("true")) 
            ? "templates/payment-failed-content.jinja" 
            : "templates/payment-failed.jinja";
        
        ctx.html(templateRenderer.render(template, context, lang));
    }
}
