# Auction Backend

This project provides the backend API for an online auction platform. It is built with **Spring Boot** and uses **PostgreSQL** for data storage. The API exposes endpoints for managing users, auctions, bids, messages, and more. Authentication is handled using JSON Web Tokens (JWT).

A live deployment is available on **Render**, so you can try the API without running it locally.

## Features

- User registration and authentication (JWT based)
- CRUD operations for auctions, categories and bids
- Messaging system between users
- Photo upload integration with Imgur
- Basic security configuration with CORS support

## Prerequisites

- Java 17
- Maven 3.x
- PostgreSQL database

## Running Locally

1. Clone the repository
   ```bash
   git clone <repo>
   cd auctionBackend
   ```
2. Configure the database connection by setting the following environment variables:
   - `DB_URL` – JDBC URL of your PostgreSQL instance
   - `DB_USERNAME` – database user
   - `DB_PASSWORD` – database password

   Example values for a local setup:
   ```bash
   export DB_URL=jdbc:postgresql://localhost:5432/auctiondb
   export DB_USERNAME=postgres
   export DB_PASSWORD=postgres
   ```

3. Start the application using the Maven wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```
   The API will be available at `http://localhost:8080`.

4. Run tests (optional):
   ```bash
   ./mvnw test
   ```

## Docker

A `Dockerfile` is included. To build and run the application in a container:

```bash
docker build -t auction-backend .

docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://<db_host>:5432/auctiondb \
  -e DB_USERNAME=<user> \
  -e DB_PASSWORD=<pass> \
  auction-backend
```

## Live Demo

The API is also hosted on **Render**. You can access the live instance to test the endpoints without any setup:

```
https://meowsuo.github.io/auctionFrontend/#/auctions
```

Replace the placeholder above with the actual Render URL of the deployed application.

## License

This project is provided under the Apache 2.0 License.
