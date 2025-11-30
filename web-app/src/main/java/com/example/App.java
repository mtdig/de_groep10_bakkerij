package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;
import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.loader.ResourceLocator;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    
    private static final List<Product> products = new ArrayList<>();
    private static final Map<String, Map<Integer, Integer>> carts = new HashMap<>(); // sessionId -> productId -> quantity
    private static final Map<String, String> users = new HashMap<>(); // sessionId -> username
    private static final Map<String, Map<String, String>> pickupDetails = new HashMap<>(); // sessionId -> pickup info
    private static final Map<String, List<Order>> orderHistory = new HashMap<>(); // username -> orders
    private static final Map<String, Address> addresses = new HashMap<>(); // username -> address
    private static final Map<String, String> lastPaymentMethod = new HashMap<>(); // sessionId -> payment method
    private static final Jinjava jinjava;
    private static final java.util.Random random = new java.util.Random();
    private static final String APP_VERSION;
    
    // Translation maps
    private static final Map<String, Map<String, String>> translations = new HashMap<>();
    
    static {
        // Load version from VERSION.txt
        String version = "unknown";
        try {
            InputStream versionStream = App.class.getClassLoader().getResourceAsStream("VERSION.txt");
            if (versionStream != null) {
                version = new String(versionStream.readAllBytes()).trim();
            }
        } catch (Exception e) {
            System.err.println("Failed to load version: " + e.getMessage());
        }
        APP_VERSION = version;
        
        // Load translations from JSON file
        loadTranslations();
    }
    
    @SuppressWarnings("unchecked")
    private static void loadTranslations() {
        try {
            InputStream translationsStream = App.class.getClassLoader().getResourceAsStream("translations.json");
            if (translationsStream == null) {
                System.err.println("Failed to load translations.json - file not found");
                return;
            }
            
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Map<String, String>> loadedTranslations = mapper.readValue(translationsStream, Map.class);
            translations.putAll(loadedTranslations);
            System.out.println("Loaded translations for languages: " + translations.keySet());
        } catch (Exception e) {
            System.err.println("Failed to load translations: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    private static void loadProducts() {
        try {
            // TODO: Get filename from environment variable
            InputStream productsStream = App.class.getClassLoader().getResourceAsStream("bread_details.json");
            if (productsStream == null) {
                System.err.println("Failed to load bread_details.json - file not found");
                return;
            }
            
            ObjectMapper mapper = new ObjectMapper();
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
            System.out.println("Loaded " + products.size() + " products from bread_details.json");
        } catch (Exception e) {
            System.err.println("Failed to load products: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static Map<String, String> getTranslations(String lang) {
        return translations.getOrDefault(lang, translations.get("nl"));
    }
    
    private static void addLangContext(Map<String, Object> context, String lang) {
        context.put("lang", lang);
        context.put("t", getTranslations(lang));
        context.put("version", APP_VERSION);
    }

    static {
        // Configure Jinjava with a resource locator to support template inheritance
        JinjavaConfig config = JinjavaConfig.newBuilder().build();
        jinjava = new Jinjava(config);
        jinjava.setResourceLocator(new ResourceLocator() {
            @Override
            public String getString(String fullName, Charset encoding, JinjavaInterpreter interpreter) throws IOException {
                // Handle both direct paths and paths from extends/include
                String path = fullName;
                if (!path.startsWith("templates/")) {
                    path = "templates/" + path;
                }
                InputStream is = App.class.getClassLoader().getResourceAsStream(path);
                if (is == null) {
                    throw new IOException("Template not found: " + path);
                }
                return new String(is.readAllBytes(), encoding);
            }
        });
    }
    
    static {
        System.out.println("Initializing bakery products...");
        loadProducts();
    }

    public static void main(String[] args) {
        // TODO: Get port from environment variable
        Javalin app = Javalin.create(config -> {
            config.router.ignoreTrailingSlashes = true;
            config.staticFiles.add("/public");
        }).start(7070);

        // Define routes - root route FIRST to ensure it takes precedence
        // TODO: group routes with routers
        app.get("/", App::getHome);
        
        app.get("/cart/count", App::getCartCount);
        app.get("/cart", App::getCart);
        app.post("/cart/add/{id}", App::addToCart);
        app.post("/cart/update/{id}", App::updateCartQuantity);
        app.delete("/cart/remove/{id}", App::removeFromCart);
        
        app.get("/products/{category}", App::getProductsByCategory);
        app.get("/products", App::getProducts);
        app.get("/product/details/{id}", App::getProductDetails);
        
        app.get("/payment", App::getPayment);
        app.get("/pickup", App::getPickup);
        app.post("/pickup/confirm", App::confirmPickup);
        app.get("/payment/success", App::getPaymentSuccess);
        app.get("/payment/failed", App::getPaymentFailed);
        
        app.get("/account", App::getAccount);
        app.post("/account/password", App::changePassword);
        app.post("/account/address", App::updateAddress);
        
        app.post("/login", App::login);
        app.post("/logout", App::logout);
        app.post("/order/repeat/{orderNumber}", App::repeatOrder);

        System.out.println("De Groep10 Bakkerij server started on http://localhost:7070");
        System.out.println("Navigate to http://localhost:7070 to view the application");
    }

    private static void getHome(Context ctx) {
        String sessionId = getSessionId(ctx);
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        Map<String, Object> context = new HashMap<>();
        addLangContext(context, lang);
        context.put("page", "home");
        context.put("cartCount", getCartCount(sessionId));
        
        // Check if this is an HTMX request, load partial if so, else full page
        String htmxRequest = ctx.header("HX-Request");
        String template = (htmxRequest != null && htmxRequest.equals("true")) 
            ? "templates/index-content.jinja" 
            : "templates/index.jinja";
        
        ctx.html(render(template, context));
    }

    private static void getProducts(Context ctx) {
        String sessionId = getSessionId(ctx);
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        Map<String, Object> context = new HashMap<>();
        context.put("products", products);
        addLangContext(context, lang);
        context.put("category", "all");
        context.put("page", "products");
        context.put("cartCount", getCartCount(sessionId));
        
        // Check if this is an HTMX request
        String htmxRequest = ctx.header("HX-Request");
        String template = (htmxRequest != null && htmxRequest.equals("true")) 
            ? "templates/products-content.jinja" 
            : "templates/products.jinja";
        
        ctx.html(render(template, context));
    }

    private static void getProductsByCategory(Context ctx) {
        String category = ctx.pathParam("category");
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        
        List<Product> filtered = category.equals("all") ? 
            products : 
            products.stream().filter(p -> p.category.equals(category)).toList();
        
        Map<String, Object> context = new HashMap<>();
        context.put("products", filtered);
        addLangContext(context, lang);
        context.put("category", category);
        ctx.html(render("templates/products-area.jinja", context));
    }

    private static void getProductDetails(Context ctx) {
        try {
            String productId = ctx.pathParam("id");
            String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
            
            // Load bread details JSON
            InputStream is = App.class.getClassLoader().getResourceAsStream("bread_details.json");
            if (is == null) {
                ctx.status(404).result("Product details not found");
                return;
            }
            
            String jsonContent = new String(is.readAllBytes());
            // Naive JSON parsing - find the product by ID
            // TODO: use Gson or Jackson
            String productJson = extractProductJson(jsonContent, productId);
            
            if (productJson == null) {
                ctx.status(404).result("Product not found");
                return;
            }
            
            ctx.contentType("application/json").result(productJson);
        } catch (Exception e) {
            ctx.status(500).result("Error loading product details");
        }
    }
    
    private static String extractProductJson(String jsonContent, String productId) {
        try {
            // Find the product object in the JSON
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

    private static void getCart(Context ctx) {
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        String sessionId = getSessionId(ctx);
        Map<Integer, Integer> cart = carts.getOrDefault(sessionId, new HashMap<>());
        
        List<Map<String, Object>> cartItems = new ArrayList<>();
        double total = 0.0;
        
        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            int productId = entry.getKey();
            int quantity = entry.getValue();
            Product product = products.stream().filter(p -> p.id == productId).findFirst().orElse(null);
            
            if (product != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("product", product);
                item.put("quantity", quantity);
                item.put("subtotal", product.price * quantity);
                cartItems.add(item);
                total += product.price * quantity;
            }
        }
        
        Map<String, Object> context = new HashMap<>();
        addLangContext(context, lang);
        context.put("cartItems", cartItems);
        context.put("total", total);
        context.put("itemCount", cart.values().stream().mapToInt(Integer::intValue).sum());
        context.put("page", "cart");
        context.put("cartCount", getCartCount(sessionId));
        
        // Check if this is an HTMX request
        String htmxRequest = ctx.header("HX-Request");
        String template = (htmxRequest != null && htmxRequest.equals("true")) 
            ? "templates/cart-content.jinja" 
            : "templates/cart.jinja";
        
        ctx.html(render(template, context));
    }

    private static void addToCart(Context ctx) {
        String sessionId = getSessionId(ctx);
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        int productId = Integer.parseInt(ctx.pathParam("id"));
        int quantity = ctx.formParam("quantity") != null ? Integer.parseInt(ctx.formParam("quantity")) : 1;
        
        Map<Integer, Integer> cart = carts.computeIfAbsent(sessionId, k -> new HashMap<>());
        cart.put(productId, cart.getOrDefault(productId, 0) + quantity);
        
        // Get product details
        Product product = products.stream()
            .filter(p -> p.id == productId)
            .findFirst()
            .orElse(null);
        
        if (product != null) {
            String productName = lang.equals("nl") ? product.nameNl : product.nameFr;
            String escapedName = productName.replace("\"", "\\\"")
                                            .replace("\n", "\\n")
                                            .replace("\r", "\\r");
            
            // Trigger modal via HX-Trigger header with product details
            ctx.header("HX-Trigger", "{\"showCartModal\":{\"lang\":\"" + lang + 
                                     "\",\"type\":\"add\",\"productName\":\"" + escapedName + 
                                     "\",\"quantity\":" + quantity + ",\"price\":" + product.price + "}}");
        } else {
            // Fallback if product not found
            ctx.header("HX-Trigger", "{\"showCartModal\":{\"lang\":\"" + lang + "\",\"type\":\"add\"}}");
        }
        
        // Return updated cart count
        ctx.html(getCartCountHtml(sessionId));
    }

    private static void updateCartQuantity(Context ctx) {
        String sessionId = getSessionId(ctx);
        int productId = Integer.parseInt(ctx.pathParam("id"));
        int quantity = Integer.parseInt(ctx.formParam("quantity"));
        
        Map<Integer, Integer> cart = carts.get(sessionId);
        if (cart != null) {
            if (quantity > 0) {
                cart.put(productId, quantity);
            } else {
                cart.remove(productId);
            }
        }
        
        // Return updated cart content for HTMX swap
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        renderCartContent(ctx, lang);
    }

    private static void removeFromCart(Context ctx) {
        String sessionId = getSessionId(ctx);
        int productId = Integer.parseInt(ctx.pathParam("id"));
        
        Map<Integer, Integer> cart = carts.get(sessionId);
        if (cart != null) {
            cart.remove(productId);
        }
        
        // Return updated cart content for HTMX swap
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        renderCartContent(ctx, lang);
    }

    private static void renderCartContent(Context ctx, String lang) {
        String sessionId = getSessionId(ctx);
        Map<Integer, Integer> cart = carts.getOrDefault(sessionId, new HashMap<>());
        
        List<Map<String, Object>> cartItems = new ArrayList<>();
        double total = 0.0;
        
        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            int productId = entry.getKey();
            int quantity = entry.getValue();
            Product product = products.stream().filter(p -> p.id == productId).findFirst().orElse(null);
            
            if (product != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("product", product);
                item.put("quantity", quantity);
                item.put("subtotal", product.price * quantity);
                cartItems.add(item);
                total += product.price * quantity;
            }
        }
        
        Map<String, Object> context = new HashMap<>();
        addLangContext(context, lang);
        context.put("cartItems", cartItems);
        context.put("total", total);
        context.put("itemCount", cart.values().stream().mapToInt(Integer::intValue).sum());
        ctx.html(render("templates/cart-content.jinja", context));
    }

    private static void getCartCount(Context ctx) {
        String sessionId = getSessionId(ctx);
        ctx.html(getCartCountHtml(sessionId));
    }

    private static String getCartCountHtml(String sessionId) {
        Map<Integer, Integer> cart = carts.getOrDefault(sessionId, new HashMap<>());
        int count = cart.values().stream().mapToInt(Integer::intValue).sum();
        return count > 0 ? "<span class=\"cart-count\">" + count + "</span>" : "";
    }

    private static int getCartCount(String sessionId) {
        Map<Integer, Integer> cart = carts.getOrDefault(sessionId, new HashMap<>());
        return cart.values().stream().mapToInt(Integer::intValue).sum();
    }

    private static String getSessionId(Context ctx) {
        String sessionId = ctx.cookie("sessionId");
        if (sessionId == null) {
            sessionId = java.util.UUID.randomUUID().toString();
            ctx.cookie("sessionId", sessionId, 86400 * 30);
        }
        return sessionId;
    }

    private static void getAccount(Context ctx) {
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        String sessionId = getSessionId(ctx);
        String username = users.get(sessionId);
        
        Map<String, Object> context = new HashMap<>();
        addLangContext(context, lang);
        context.put("page", "account");
        context.put("cartCount", getCartCount(sessionId));
        
        if (username != null) {
            // User is logged in - show order history
            List<Order> orders = orderHistory.get(username);
            Address address = addresses.get(username);
            context.put("username", username);
            context.put("orders", orders);
            context.put("address", address);
        }
        
        // Check if this is an HTMX request
        String htmxRequest = ctx.header("HX-Request");
        String template = (htmxRequest != null && htmxRequest.equals("true")) 
            ? "templates/account-content.jinja" 
            : "templates/account.jinja";
        
        ctx.html(render(template, context));
    }

    private static void login(Context ctx) {
        String firstname = ctx.formParam("firstname");
        String password = ctx.formParam("password");
        
        // Simple validation: firstname must be a word (letters only), password can be anything
        if (firstname != null && !firstname.trim().isEmpty() && firstname.matches("[a-zA-Z]+")) {
            String sessionId = getSessionId(ctx);
            users.put(sessionId, firstname);
            
            // Generate order history if not exists
            if (!orderHistory.containsKey(firstname)) {
                orderHistory.put(firstname, generateOrderHistory(firstname));
            }
            
            ctx.redirect("/account?lang=" + ctx.formParam("lang"));
        } else {
            ctx.redirect("/account?lang=" + ctx.formParam("lang") + "&error=invalid");
        }
    }

    private static void logout(Context ctx) {
        String sessionId = getSessionId(ctx);
        users.remove(sessionId);
        ctx.redirect("/account?lang=" + ctx.formParam("lang"));
    }

    private static void changePassword(Context ctx) {
        String sessionId = getSessionId(ctx);
        String username = users.get(sessionId);
        String lang = ctx.formParam("lang") != null ? ctx.formParam("lang") : "nl";
        
        if (username == null) {
            ctx.status(401).html("<div class=\"error-message\">" + 
                (lang.equals("nl") ? "Niet ingelogd" : "Non connecté") + "</div>");
            return;
        }
        
        String newPassword = ctx.formParam("newPassword");
        String confirmPassword = ctx.formParam("confirmPassword");
        
        // TODO: move errors to translations.json
        if (!newPassword.equals(confirmPassword)) {
            ctx.html("<div class=\"error-message\">" + 
                (lang.equals("nl") ? "Wachtwoorden komen niet overeen" : "Les mots de passe ne correspondent pas") + "</div>");
            return;
        }
        
        // fake password change
        ctx.html("<div class=\"success-message\">" + 
            (lang.equals("nl") ? "Wachtwoord succesvol gewijzigd!" : "Mot de passe modifié avec succès!") + "</div>");
    }

    private static void updateAddress(Context ctx) {
        String sessionId = getSessionId(ctx);
        String username = users.get(sessionId);
        String lang = ctx.formParam("lang") != null ? ctx.formParam("lang") : "nl";
        
        if (username == null) {
            ctx.status(401).html("<div class=\"error-message\">" + 
                (lang.equals("nl") ? "Niet ingelogd" : "Non connecté") + "</div>");
            return;
        }
        
        String street = ctx.formParam("street");
        String postal = ctx.formParam("postal");
        String city = ctx.formParam("city");
        String country = ctx.formParam("country");
        
        Address address = new Address(street, postal, city, country);
        addresses.put(username, address);
        
        ctx.html("<div class=\"success-message\">" + 
            (lang.equals("nl") ? "Adres succesvol opgeslagen!" : "Adresse enregistrée avec succès!") + "</div>");
    }

    private static void getPickup(Context ctx) {
        String sessionId = getSessionId(ctx);
        String username = users.get(sessionId);
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        
        // Check if user is logged in
        if (username == null) {
            ctx.redirect("/account?lang=" + lang);
            return;
        }
        
        // Calculate min and max dates (today + 1 to today + 30)
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate minDate = today.plusDays(1);
        java.time.LocalDate maxDate = today.plusDays(30);
        
        Map<String, Object> context = new HashMap<>();
        addLangContext(context, lang);
        context.put("page", "pickup");
        context.put("minDate", minDate.toString());
        context.put("maxDate", maxDate.toString());
        
        String htmxRequest = ctx.header("HX-Request");
        String template = (htmxRequest != null && htmxRequest.equals("true")) 
            ? "templates/pickup-content.jinja" 
            : "templates/pickup.jinja";
        
        ctx.html(render(template, context));
    }
    
    private static void confirmPickup(Context ctx) {
        String sessionId = getSessionId(ctx);
        String pickupDate = ctx.formParam("pickupDate");
        String pickupTime = ctx.formParam("pickupTime");
        
        // Store pickup details in session (using a simple map)
        if (!pickupDetails.containsKey(sessionId)) {
            pickupDetails.put(sessionId, new HashMap<>());
        }
        pickupDetails.get(sessionId).put("date", pickupDate);
        pickupDetails.get(sessionId).put("time", pickupTime);
        
        // Redirect to payment
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        getPayment(ctx);
    }

    private static void getPayment(Context ctx) {
        String sessionId = getSessionId(ctx);
        String username = users.get(sessionId);
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        String retry = ctx.queryParam("retry");
        
        // Check if user is logged in
        if (username == null) {
            // Redirect to account page to login
            ctx.redirect("/account?lang=" + lang);
            return;
        }
        
        Map<String, Object> context = new HashMap<>();
        addLangContext(context, lang);
        context.put("page", "payment");
        context.put("retry", retry != null && retry.equals("true"));
        context.put("lastPaymentMethod", lastPaymentMethod.getOrDefault(sessionId, ""));
        
        // Check if this is an HTMX request
        String htmxRequest = ctx.header("HX-Request");
        String template = (htmxRequest != null && htmxRequest.equals("true")) 
            ? "templates/payment-content.jinja" 
            : "templates/payment.jinja";
        
        ctx.html(render(template, context));
    }

    private static void getPaymentSuccess(Context ctx) {
        String sessionId = getSessionId(ctx);
        String username = users.get(sessionId);
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        String paymentMethod = ctx.queryParam("method");
        boolean isCashPayment = "cash".equals(paymentMethod);
        
        // Get cart items and add to order history
        Map<Integer, Integer> cart = carts.get(sessionId);
        if (cart != null && !cart.isEmpty() && username != null) {
            // Create order from cart
            List<OrderItem> orderItems = new ArrayList<>();
            double total = 0.0;
            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                int productId = entry.getKey();
                int quantity = entry.getValue();
                Product product = products.stream()
                    .filter(p -> p.id == productId)
                    .findFirst()
                    .orElse(null);
                if (product != null) {
                    orderItems.add(new OrderItem(product, quantity));
                    total += product.price * quantity;
                }
            }
            
            // Add order to history
            int orderNumber = random.nextInt(90000) + 10000; // 5-digit order number
            String orderDate = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            Order order = new Order(orderNumber, orderDate, orderItems, total);
            
            List<Order> userOrders = orderHistory.computeIfAbsent(username, k -> new ArrayList<>());
            userOrders.add(0, order); // Add to beginning of list
            
            // Clear the cart
            cart.clear();
        }
        
        Map<String, Object> context = new HashMap<>();
        addLangContext(context, lang);
        context.put("page", "payment");
        context.put("isCashPayment", isCashPayment);
        
        // Check if this is an HTMX request
        String htmxRequest = ctx.header("HX-Request");
        String template = (htmxRequest != null && htmxRequest.equals("true")) 
            ? "templates/payment-success-content.jinja" 
            : "templates/payment-success.jinja";
        
        ctx.html(render(template, context));
    }

    private static void getPaymentFailed(Context ctx) {
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        Map<String, Object> context = new HashMap<>();
        addLangContext(context, lang);
        context.put("page", "payment");
        
        // Check if this is an HTMX request
        String htmxRequest = ctx.header("HX-Request");
        String template = (htmxRequest != null && htmxRequest.equals("true")) 
            ? "templates/payment-failed-content.jinja" 
            : "templates/payment-failed.jinja";
        
        ctx.html(render(template, context));
    }

    private static void repeatOrder(Context ctx) {
        String sessionId = getSessionId(ctx);
        String username = users.get(sessionId);
        String lang = ctx.queryParam("lang") != null ? ctx.queryParam("lang") : "nl";
        
        if (username == null) {
            ctx.status(401).result("Not logged in");
            return;
        }
        
        int orderNumber = Integer.parseInt(ctx.pathParam("orderNumber"));
        List<Order> orders = orderHistory.get(username);
        
        if (orders == null) {
            ctx.status(404).result("No orders found");
            return;
        }
        
        // Find the order by order number
        Order orderToRepeat = orders.stream()
            .filter(o -> o.orderNumber == orderNumber)
            .findFirst()
            .orElse(null);
        
        if (orderToRepeat == null) {
            ctx.status(404).result("Order not found");
            return;
        }
        
        // Add all items from the order to the cart
        Map<Integer, Integer> cart = carts.computeIfAbsent(sessionId, k -> new HashMap<>());
        for (OrderItem item : orderToRepeat.items) {
            cart.put(item.product.id, cart.getOrDefault(item.product.id, 0) + item.quantity);
        }
        
        // Trigger modal via HX-Trigger header
        ctx.header("HX-Trigger", "{\"showCartModal\":{\"lang\":\"" + lang + "\",\"type\":\"repeat\"}}");
        
        // Return updated cart count
        ctx.html(getCartCountHtml(sessionId));
    }

    private static List<Order> generateOrderHistory(String username) {
        List<Order> orders = new ArrayList<>();
        int orderCount = random.nextInt(7); // 0-6 orders
        
        // Only use "brood" category products
        List<Product> broodProducts = products.stream()
            .filter(p -> "brood".equals(p.category))
            .toList();
        
        for (int i = 0; i < orderCount; i++) {
            List<OrderItem> items = new ArrayList<>();
            int itemCount = random.nextInt(200) + 1; // 1-200 products
            
            // Distribute products across the order
            int itemsRemaining = itemCount;
            while (itemsRemaining > 0) {
                Product product = broodProducts.get(random.nextInt(broodProducts.size()));
                int quantity = Math.min(random.nextInt(20) + 1, itemsRemaining);
                items.add(new OrderItem(product, quantity));
                itemsRemaining -= quantity;
            }
            
            // Calculate total
            double total = items.stream()
                .mapToDouble(item -> item.product.price * item.quantity)
                .sum();
            
            // Generate date (random past date within last year)
            long daysAgo = random.nextInt(365);
            String date = java.time.LocalDate.now().minusDays(daysAgo).toString();
            
            orders.add(new Order(orderCount - i, date, items, total));
        }
        
        return orders;
    }

    private static String render(String templatePath, Map<String, Object> context) {
        try {
            InputStream is = App.class.getClassLoader().getResourceAsStream(templatePath);
            if (is == null) {
                throw new RuntimeException("Template not found: " + templatePath);
            }
            String template = new String(is.readAllBytes());
            return jinjava.render(template, context);
        } catch (Exception e) {
            throw new RuntimeException("Failed to render template: " + templatePath, e);
        }
    }
}
