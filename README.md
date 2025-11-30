
![De Bakkerij](images/hero.png)

-> [README.md](web-app/README.md)

-> [breadandbytes.be](https://breadandbytes.be)



## Getting started

Install [Git](https://git-scm.com/install/) if you don't already have it.

Install [Java (21 or newer)](https://www.java.com/en/download/manual.jsp) if you don't already have it. (Sorry, friends let friends use java.)

Change into a project directory of your choice and clone the project.

```bash
mini@pi5:~/projects$ git clone https://github.com/mtdig/de_groep10_bakkerij.git
Cloning into 'de_groep10_bakkerij'...
remote: Enumerating objects: 157, done.
remote: Counting objects: 100% (157/157), done.
remote: Compressing objects: 100% (120/120), done.
remote: Total 157 (delta 33), reused 148 (delta 24), pack-reused 0 (from 0)
Receiving objects: 100% (157/157), 2.30 MiB | 14.63 MiB/s, done.
Resolving deltas: 100% (33/33), done.
mini@pi5:~/projects$
```

**Change into the web-app subdir.**

```bash
mini@pi5:~/projects/de_groep10_bakkerij$ cd web-app/
mini@pi5:~/projects/de_groep10_bakkerij/web-app$
```

Build
```bash
mvn clean package
```

Run
```
mvn exec:java -Dexec.mainClass="com.example.bakkerij.Application"
```

Success!
```bash
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  10.760 s
[INFO] Finished at: 2025-11-30T15:28:40+01:00
[INFO] ------------------------------------------------------------------------
mvn exec:java -Dexec.mainClass="com.example.bakkerij.Application"
[INFO] Scanning for projects...
[INFO]
[INFO] -------------------< com.example:degroep10bakkerij >--------------------
[INFO] Building degroep10bakkerij 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- exec-maven-plugin:3.6.2:java (default-cli) @ degroep10bakkerij ---
Loaded translations for languages: [en, de, fr, nl, es, zh]
Initializing bakery products...
Loaded 9 products from bread_details.json
[com.example.bakkerij.Application.main()] INFO io.javalin.Javalin - Starting Javalin ...
[com.example.bakkerij.Application.main()] INFO org.eclipse.jetty.server.Server - jetty-11.0.25; built: 2025-03-13T00:15:57.301Z; git: a2e9fae3ad8320f2a713d4fa29bba356a99d1295; jvm 21.0.1+12-LTS
[com.example.bakkerij.Application.main()] INFO org.eclipse.jetty.server.session.DefaultSessionIdManager - Session workerName=node0
[com.example.bakkerij.Application.main()] INFO org.eclipse.jetty.server.handler.ContextHandler - Started o.e.j.s.ServletContextHandler@4e61c225{/,null,AVAILABLE}
[com.example.bakkerij.Application.main()] INFO org.eclipse.jetty.server.AbstractConnector - Started ServerConnector@4bf533bb{HTTP/1.1, (http/1.1)}{0.0.0.0:7071}
[com.example.bakkerij.Application.main()] INFO org.eclipse.jetty.server.Server - Started Server@43a10722{STARTING}[11.0.25,sto=0] @3615ms
[com.example.bakkerij.Application.main()] INFO io.javalin.Javalin -
       __                  ___           _____
      / /___ __   ______ _/ (_)___      / ___/
 __  / / __ `/ | / / __ `/ / / __ \    / __ \
/ /_/ / /_/ /| |/ / /_/ / / / / / /   / /_/ /
\____/\__,_/ |___/\__,_/_/_/_/ /_/    \____/

       https://javalin.io/documentation

[com.example.bakkerij.Application.main()] INFO io.javalin.Javalin - Javalin started in 233ms \o/
[com.example.bakkerij.Application.main()] INFO io.javalin.Javalin - Static file handler added: StaticFileConfig(hostedPath=/, directory=/public, location=CLASSPATH, precompress=false, aliasCheck=null, headers={Cache-Control=max-age=0}, skipFileFunction=Function1<jakarta.servlet.http.HttpServletRequest, java.lang.Boolean>, mimeTypes={}, roles=[]). File system location: 'file:///home/mini/projects/de_groep10_bakkerij/web-app/target/classes/public/'
[com.example.bakkerij.Application.main()] INFO io.javalin.Javalin - Listening on http://localhost:7071/
[com.example.bakkerij.Application.main()] INFO io.javalin.Javalin - You are running Javalin 6.7.0 (released June 22, 2025. Your Javalin version is 160 days old. Consider checking for a newer version.).
De Groep10 Bakkerij server started on http://localhost:7071
Navigate to http://localhost:7071 to view the application
```
