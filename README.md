# TalentTauscher

TalentTauscher is a Java based web application that allows users to trade skills and services with each other using a virtual currency called *Talent points*. It was implemented as part of a university software engineering project and demonstrates a full stack Jakarta EE application.

## Features
- Browse public advertisements with category filtering and pagination.
- Register with email verification and manage your personal profile.
- Create, edit and delete advertisements including optional images.
- Request services from other users and exchange messages.
- Spend and earn Talent points for accepted requests.
- Administration console for user and category management, privacy policy and imprint pages.
- Internationalization support for German and English.
- Extensive unit, integration and system tests.

## Technology Stack
- Java 21 with Jakarta EE (JSF, CDI)
- Maven build system
- PostgreSQL database
- Apache Tomcat 10
- JUnit 5 and Selenium for testing

## Repository Layout
- `src/main/java` – application source code
- `src/main/resources` – message bundles and SQL scripts
- `src/main/webapp` – JSF views and configuration
- `src/test` – automated tests
- `docs` – user manual and additional documentation

## Setup
1. Install Java 21 and Maven.
2. Configure the database and SMTP settings in `src/main/webapp/WEB-INF/config/application.properties`. Environment variables should be used for credentials.
3. Build the project:
   ```bash
   mvn clean package
   ```
4. Deploy the resulting WAR file to Apache Tomcat 10 or newer.

Detailed installation instructions can be found in `docs/Handbuch/install.md`.

## Running Tests
Execute `mvn test` to run the unit tests. Integration and system tests are executed during `mvn verify`.

## Documentation
The `docs` folder contains further PDFs and guides such as the user manual (`docs/Handbuch/handbuch.html`) and the installation guide. These documents provide a complete overview of the functionality and how to operate the application.
