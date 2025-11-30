# Architecture Diagram

## Refactored Application Structure

```
┌─────────────────────────────────────────────────────────────┐
│                      HTTP Layer                              │
│  (Javalin Routes + HTMX)                                    │
└────────────────────┬────────────────────────────────────────┘
                     │
         ┌───────────┴───────────┐
         │   Application.java    │  ← Wires everything together
         │  (Dependency Setup)   │
         └───────────┬───────────┘
                     │
        ┌────────────┴────────────┐
        │                         │
┌───────▼─────────┐      ┌───────▼──────────┐
│   Handlers      │      │   Utilities      │
│  (HTTP Layer)   │      │  (Cross-cutting) │
├─────────────────┤      ├──────────────────┤
│ • HomeHandler   │      │ • TemplateRender │
│ • ProductHandler│      │ • TranslationSvc │
│ • CartHandler   │      │ • SessionManager │
│ • AccountHandler│      └──────────────────┘
│ • PaymentHandler│
└────────┬────────┘
         │
         │ delegates to
         │
┌────────▼─────────┐
│   Services       │
│ (Business Logic) │
├──────────────────┤
│ • ProductService │
│ • CartService    │
│ • OrderService   │
│ • UserService    │
└────────┬─────────┘
         │
         │ uses
         │
┌────────▼──────────┐
│   Repositories    │
│  (Data Access)    │
├───────────────────┤
│ • ProductRepo     │
│ • CartRepo        │
│ • OrderRepo       │
│ • UserRepo        │
└────────┬──────────┘
         │
         │ manages
         │
┌────────▼──────────┐
│     Models        │
│  (Domain Objects) │
├───────────────────┤
│ • Product         │
│ • Cart            │
│ • Order           │
│ • OrderItem       │
│ • Address         │
└───────────────────┘
```

## Request Flow Example

```
User clicks "Add to Cart"
       │
       ▼
┌──────────────────────┐
│  POST /cart/add/{id} │  HTTP Request
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│   CartHandler        │  Extract params, session
│   .addToCart()       │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│   CartService        │  Business logic
│   .addToCart()       │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│   CartRepository     │  Data access
│   .getCart()         │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│   Cart Model         │  Domain object
│   .addItem()         │
└──────────┬───────────┘
           │
           ▼
    Response rendered
```

## Key Benefits

### 1. Separation of Concerns
Each layer has a distinct responsibility:
- **Handlers**: Handle HTTP, extract params, return responses
- **Services**: Implement business logic, coordinate operations
- **Repositories**: Manage data storage and retrieval
- **Models**: Represent domain concepts

### 2. Dependency Flow (Clean Architecture)
```
Handlers → Services → Repositories → Models
         ↓
    Utilities
```
- Dependencies point inward
- Inner layers don't know about outer layers
- Easy to swap implementations

### 3. Testability
```java
// Test CartService without HTTP layer
CartService service = new CartService(
    mockCartRepo,
    mockProductService
);

// Test CartHandler with mock service
CartHandler handler = new CartHandler(
    mockCartService,
    mockProductService,
    mockRenderer,
    mockSession
);
```

## Comparison

### Before (Monolithic)
```
App.java (750 lines)
  ├── HTTP handling
  ├── Business logic
  ├── Data storage
  ├── Template rendering
  ├── Session management
  └── All mixed together ❌
```

### After (Layered)
```
Application.java (100 lines)
  └── Wires dependencies

Handlers (5 classes)
  └── HTTP concerns only

Services (4 classes)
  └── Business logic only

Repositories (4 classes)
  └── Data access only

Models (5 classes)
  └── Domain objects only

Utilities (3 classes)
  └── Cross-cutting concerns

✅ Clear responsibilities
✅ Easy to understand
✅ Easy to test
✅ Easy to extend
```
