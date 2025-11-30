# De Groep10 Bakkerij

A super simple, fast Single Page Application built with:
- **Java** (no Spring!) - using Javalin microframework
- [**Jinjava**](https://github.com/HubSpot/jinjava) - real Jinja2 templating for Java
- **HTMX** - Awesome library for dynamic updates without writing JavaScript
- **Minimal CSS** - clean and simple styling (with the help of ai for restructuring, completing, ..), bootstrap would have saved time, tailwindcss would have saved size

## Why This Stack?

- **Javalin**: Extremely lightweight (just 200KB), built on Jetty
- **Jinjava**: Actual Jinja2 syntax - what we use with Python/Flask/FastApi/Django
- **HTMX**: HTML-driven interactivity, no complex JS framework needed
- **No frameworks**: Fast startup, minimal dependencies, easy to understand



## Project Structure
```
web-app/
├── pom.xml
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/
│       │       └──> Application code
│       └── resources/
│           ├── templates/
│           │   ├──> jinja2 templates, the website
│           └── public/
│               └── static content: css, javascript, images
```


## Running the App

### Prerequisites
- Java 21 or higher
- Maven

#### additionally
- Docker / Docker Desktop
- Make
### Steps
#### Local, native
1. **Build the project:**
   ```bash
   mvn clean package
   ```

2. **Run the application:**
   ```bash
   mvn exec:java -Dexec.mainClass="com.example.App"
   ```

3. **Open your browser:**
   ```
   http://localhost:7070
   ```

#### Local, Make
1. **Build the project:**
   ```bash
   make build
   ```

2. **Run the application:**
   ```bash
   make run
   ```

3. **Open your browser:**
   ```
   http://localhost:7070
   ```

## How It Works

### HTMX Attributes Used:

- `hx-get` - Fetches content from server
- `hx-post` - Sends form data
- `hx-delete` - Sends delete request
- `hx-put` - Sends update request
- `hx-target` - Specifies which element to update
- `hx-swap` - Controls how content is swapped
- `hx-trigger` - When to trigger the request


