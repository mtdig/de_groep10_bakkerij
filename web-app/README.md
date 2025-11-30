# De Groep10 Bakkerij

A super simple, fast Single Page Application built with:
- **Java** (no Spring!) - using Javalin microframework
- [**Jinjava**](https://github.com/HubSpot/jinjava) - real Jinja2 templating for Java
- [**HTMX**](https://htmx.org/) - Awesome library for dynamic updates without writing JavaScript
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

### [Javalin](https://javalin.io/documentation)

Lightweight and fast webserver, built on top of [Jetty](https://jetty.org/index.html), for java and kotlin.

### [HTMX](https://htmx.org)

Extends html with dynamic/active keywords: hx-get, hx-post, ...
The idea is make any html component dynamic and to only update parts of the DOM.
This helps me keep everything together where that makes sense.  For simple things, we don't need yet another application in yet another programming language.

### [jinja2](https://jinja.palletsprojects.com/en/stable/)
Powerful and popular templating framework best known in the python world. It supports basic programming logic, allowing for conditional html.

```jinja
<select name="product" id="products">
{% for item in items %}
   <option value="{{ item.name }}">{{ item.description }}</option>
{% endfor %}
</select>
```

### [jinjava](https://github.com/HubSpot/jinjava)
Jinja2 template rendering engine for java.

```java
context.put("items", items);
jinjava.render(template, context);
```

### [Docker (Desktop)](https://www.docker.com/products/docker-desktop/)

Containers.  Independent.  Smaller than virtual machines, larger than wasm.


### [Make](https://makefiletutorial.com/)

Command runner, historically used for compiling and linking C code.  I still use it as an extra layer on top of go, rust, ..
There might be a better alternative: [just](https://github.com/casey/just)

