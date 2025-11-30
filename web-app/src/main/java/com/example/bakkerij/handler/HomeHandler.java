package com.example.bakkerij.handler;

import com.example.bakkerij.service.CartService;
import com.example.bakkerij.util.SessionManager;
import com.example.bakkerij.util.TemplateRenderer;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class HomeHandler {
    private final TemplateRenderer templateRenderer;
    private final SessionManager sessionManager;
    private final CartService cartService;

    public HomeHandler(TemplateRenderer templateRenderer, SessionManager sessionManager, CartService cartService) {
        this.templateRenderer = templateRenderer;
        this.sessionManager = sessionManager;
        this.cartService = cartService;
    }

    public void getHome(Context ctx) {
        String sessionId = sessionManager.getOrCreateSessionId(ctx);
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        
        Map<String, Object> context = new HashMap<>();
        context.put("page", "home");
        context.put("cartCount", cartService.getCartCount(sessionId));
        
        String htmxRequest = ctx.header("HX-Request");
        String template = (htmxRequest != null && htmxRequest.equals("true")) 
            ? "templates/index-content.jinja" 
            : "templates/index.jinja";
        
        ctx.html(templateRenderer.render(template, context, lang));
    }
}
