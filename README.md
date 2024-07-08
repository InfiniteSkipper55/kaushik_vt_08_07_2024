# URL Shortener

This is a URL Shortener project built with Java and Spring Boot. The application generates a shorter and unique alias for long URLs, redirects to the original link, and allows updating the original URL and its expiration date.

## Features

- Generate short URLs for long URLs
- Redirect to the original URL using the short URL
- Update the original URL
- Update the expiration date of the short URL
- Highly available and minimal latency for real-time redirection

## Requirements

- Java 11 or higher
- Maven
- Docker (optional, for containerization)

## Getting Started

### Clone the Repository

```sh
git clone https://github.com/InfiniteSkipper55/kaushik_vt_08_07_2024.git
cd kaushik_vt_08_07_2024
```

## Build the Project
Ensure you have Maven installed and then build the project:

```sh
mvn clean package
```

## Run the Application
Run the application using the Spring Boot plugin:

```sh
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

## API Endpoints
### Shorten URL

```bash
POST /shorten
```

### Request Parameters:

- `originalUrl` (String): The original long URL to shorten.

### Example Request:

```sh
curl -X POST "http://localhost:8080/shorten?originalUrl=https://www.example.com"
```

## Redirect to Original URL
```sql
GET /{shortUrl}
```

### Request Parameters:

- `shortUrl` (String): The shortened URL path.

### Example Request:

```sh
curl -L "http://localhost:8080/abc123"
```

## Update Short URL
```bash
POST /update
```

### Request Parameters:

- `shortUrl` (String): The shortened URL path.
- `newOriginalUrl` (String): The new original URL to update.


### Example Request:

```sh
curl -X POST "http://localhost:8080/update?shortUrl=abc123&newOriginalUrl=https://www.newexample.com"
```

## Update Expiry Date
```bash
POST /update-expiry
```

### Request Parameters:

- `shortUrl` (String): The shortened URL path.
- `daysToAdd` (int): The number of days to add to the current expiration date.

### Example Request:

```sh
curl -X POST "http://localhost:8080/update-expiry?shortUrl=abc123&daysToAdd=30"
```

# Docker
## Build Docker Image
To build a Docker image for this project:

```sh
docker build -t url-shortener .
```

## Run Docker Container
To run the Docker container:

```sh
docker run -p 8080:8080 url-shortener
```
