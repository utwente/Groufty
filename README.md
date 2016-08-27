# Javalon Groufty Readme 

This readme file briefly explains how to deploy Javalon Groufty. Should you incur any difficulties, do not hesitate to contact us.

## Requirements
### Execution requirements
* Java Runtime Environment (JRE) 8+
* Postgresql (recommended and default in production mode, but not required)

### Development requirements
* Java Development Kit (JDK) 8+
* Maven 3.1+

## Production requirements
* PostgreSQL
* `CREATE EXTENSION IF NOT EXISTS lo` on any schema the app is deployed.

### What about node.js and npm?
Both node.js and npm are used in groufty, however you do not need to have either installed on your system. When performing any maven task like `mvn spring-boot:run` or `mvn package`, an instance of nodejs will be downloaded and placed in the `target` folder. This is to ensure cross-platform performance and minimize external dependencies. This will however negatively impact the performance of the first maven task you execute.

## Compiling and running
This section describes how to compile and run Groufty. Note that Maven version 3.1+ is a necessity here, older versions will not work. Maven is set up with two build profiles; a **development** (`dev`) and a **production** (`prod`) profile. The dev profile will print more log messages and use an embedded H2SQL server  accessible at `http://localhost:8080/h2-console/`. The production profile is less verbose in logging and will try to connect to a PostgreSQL server. By default the **development** profile is active. If you wish to activate the production protocol add `-P prod` to your maven commands.

To compile and run Groufty in one action; it is possible to make use of the integrated Maven Spring Boot plugin; this can be done by running `mvn spring-boot:run`. This will compile the code and run the application afterwards, accessible from your browser of choice at `localhost:8080`.
Alternatively; it is also possible to manually perform the actions done by the Maven Spring Boot plugin. Depending on your set-up you can compile Groufty using an IDE (e.g. Eclipse, Intellij, Netbeans) or from the command line.

### Compiling from an IDE
Be sure to run Maven with the goal `generate-resources` before compiling the application from within the IDE. This will compile the TypeScript files in the `src/main/web/ts` directory to `src/main/resources/static/js`. Afterwards, glup will copy the static css and html from `src/main/web/static` to `src/main/resources/static`. It will also make sure the needed JavaScript libraries are copied from their npm folders to `src/main/resources/static/js/libs`. You can now run the `nl.javalon.groufty.Application` class from within your IDE. If you make changes to the Typescript or other client-side files, make sure you execute `mvn generate-resources` again.

### Compiling from the command line
Compiling Groufty from the command line is a simple as `mvn package` which will produce a fat JAR, containing all dependencies. If you do this in development mode (default), the dev dependencies will also be included. The fat jar can be found in `./target/groufty-0.0.1-SNAPSHOT.jar`

### Running tests
To run the unit tests, perform `mvn test`. Note that tests will also be executed when creating a package.
### Running Groufty
The JAR produced by Maven may be run using Java like so;
```java -jar JARNAME```

After starting the application you can reach the interface by browsing to localhost:8080 from your favorite web browser.

#### Custom configurations
If you wish to run Groufty on a different port, wish to configure a different database server or simply wish to provide database credentials (which is required in production mode), you can do so via command line arguments specified at runtime.
For instance, to set the port number to 8888, launch Groufty as follows:
```java -jar JARNAME --server.port=8888```. A comprehensive list of properties can be found on the Spring Boot website: https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html

In order to use Postgres, various properties in spring.datasource like user and password have to be set, otherwise the application will fail to start.

## API endpoints
RESTful API endpoints can be reached at /api/v1. Elaborate documentation on the precise usage of the endpoints is available at /swagger-ui.html
API endpoints use the common HTTP verbs GET, POST, PUT and DELETE. Any information to be consumed by the API is to be provided in UTF-8 encoded JSON.
All information produced by the API is emitted in this format as well.