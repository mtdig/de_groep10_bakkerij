package com.example.bakkerij;

import com.example.bakkerij.handler.*;
import com.example.bakkerij.repository.*;
import com.example.bakkerij.service.*;
import com.example.bakkerij.util.*;
import io.javalin.Javalin;

import java.io.InputStream;

public class Application {
    
    private static final String APP_VERSION = loadVersion();
    
    private static String loadVersion() {
        try {
            InputStream versionStream = Application.class.getClassLoader().getResourceAsStream("VERSION.txt");
            if (versionStream != null) {
                return new String(versionStream.readAllBytes()).trim();
            }
        } catch (Exception e) {
            System.err.println("Failed to load version: " + e.getMessage());
        }
        return "unknown";
    }

    public static void main(String[] args) {
        // Initialize repositories
        ProductRepository productRepository = new ProductRepository();
        CartRepository cartRepository = new CartRepository();
        OrderRepository orderRepository = new OrderRepository();
        UserRepository userRepository = new UserRepository();
        
        // Initialize services
        ProductService productService = new ProductService(productRepository);
        CartService cartService = new CartService(cartRepository, productService);
        OrderService orderService = new OrderService(orderRepository, productService);
        UserService userService = new UserService(userRepository);
        
        // Initialize utilities
        TranslationService translationService = new TranslationService();
        translationService.loadTranslations("translations.json");
        
        TemplateRenderer templateRenderer = new TemplateRenderer(translationService, APP_VERSION);
        SessionManager sessionManager = new SessionManager();
        
        // Initialize handlers
        HomeHandler homeHandler = new HomeHandler(templateRenderer, sessionManager, cartService);
        ProductHandler productHandler = new ProductHandler(productService, cartService, templateRenderer, sessionManager);
        CartHandler cartHandler = new CartHandler(cartService, productService, templateRenderer, sessionManager);
        AccountHandler accountHandler = new AccountHandler(userService, orderService, cartService, templateRenderer, sessionManager);
        PaymentHandler paymentHandler = new PaymentHandler(userService, orderService, cartService, templateRenderer, sessionManager);
        
        // Load data
        System.out.println("Initializing bakery products...");
        productRepository.loadProducts("bread_details.json");
        
        // Create and configure Javalin app
        Javalin app = Javalin.create(config -> {
            config.router.ignoreTrailingSlashes = true;
            config.staticFiles.add("/public");
        }).start(7070);

        // Configure routes
        configureRoutes(app, homeHandler, productHandler, cartHandler, accountHandler, paymentHandler);

        System.out.println("De Groep10 Bakkerij server started on http://localhost:7070");
        System.out.println("Navigate to http://localhost:7070 to view the application");
    }

    private static void configureRoutes(Javalin app, HomeHandler homeHandler, ProductHandler productHandler,
                                       CartHandler cartHandler, AccountHandler accountHandler, 
                                       PaymentHandler paymentHandler) {
        // Home
        app.get("/", homeHandler::getHome);
        
        // Cart routes
        app.get("/cart/count", cartHandler::getCartCount);
        app.get("/cart", cartHandler::getCart);
        app.post("/cart/add/{id}", cartHandler::addToCart);
        app.post("/cart/update/{id}", cartHandler::updateCartQuantity);
        app.delete("/cart/remove/{id}", cartHandler::removeFromCart);
        
        // Product routes
        app.get("/products/{category}", productHandler::getProductsByCategory);
        app.get("/products", productHandler::getProducts);
        app.get("/product/details/{id}", productHandler::getProductDetails);
        
        // Payment routes
        app.get("/payment", paymentHandler::getPayment);
        app.get("/pickup", paymentHandler::getPickup);
        app.post("/pickup/confirm", paymentHandler::confirmPickup);
        app.get("/payment/success", paymentHandler::getPaymentSuccess);
        app.get("/payment/failed", paymentHandler::getPaymentFailed);
        
        // Account routes
        app.get("/account", accountHandler::getAccount);
        app.post("/account/password", accountHandler::changePassword);
        app.post("/account/address", accountHandler::updateAddress);
        app.post("/login", accountHandler::login);
        app.post("/logout", accountHandler::logout);
        app.post("/order/repeat/{orderNumber}", accountHandler::repeatOrder);
    }
}
