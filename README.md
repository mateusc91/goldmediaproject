# Video Metadata Service

A modular, secure backend application for managing and analyzing video metadata from multiple platforms.

## Features

- JWT-based authentication with admin and user roles
- Video metadata management (import, query, statistics)
- Role-based access control
- API documentation with Swagger/OpenAPI
- Caching for statistics with Caffeine
- Pagination and sorting for all list endpoints
- Background/asynchronous video imports with job tracking
- Docker support for easy deployment

## Technologies

- Java 17
- Spring Boot 3.5.3
- Spring Security with JWT
- Spring Data JPA
- H2 Database (in-memory)
- Swagger/OpenAPI for documentation
- JUnit 5 and Mockito for testing
- Caffeine for caching
- Spring Async for background processing
- Docker for containerization

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application using Maven:

```bash
./mvnw spring-boot:run
```

The application will start on port 8080 by default.

## API Documentation

Once the application is running, you can access the Swagger UI at:

```
http://localhost:8080/swagger-ui/index.html
```

This provides interactive documentation for all available endpoints.

## Authentication

The application uses JWT-based authentication. There are two predefined users:

- Admin user:
  - Username: `admin`
  - Password: `admin123`
  - Roles: `ADMIN`, `USER`

- Regular user:
  - Username: `user`
  - Password: `user123`
  - Role: `USER`

### How to Authenticate

1. Send a POST request to `/auth/login` with your credentials:

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

2. You will receive a response with a JWT token:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "roles": ["ADMIN", "USER"]
}
```

3. Use this token in the Authorization header for subsequent requests:

```bash
curl -X GET http://localhost:8080/videos \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

## API Usage Examples

### Import Videos (Admin only)

```bash
curl -X POST http://localhost:8080/videos/import?count=5 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### Get All Videos

```bash
curl -X GET http://localhost:8080/videos \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### Get Videos by Source

```bash
curl -X GET http://localhost:8080/videos?source=YouTube \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### Create a Video (Admin only)

```bash
curl -X POST http://localhost:8080/videos/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -d '{
    "title": "My New Video",
    "description": "This is a description of my video",
    "url": "https://example.com/video123",
    "source": "YouTube",
    "uploadDate": "2023-01-15",
    "durationInSeconds": 600
  }'
```

### Get Video Statistics

```bash
curl -X GET http://localhost:8080/videos/stats \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

## Advanced Features

### Pagination and Sorting

The API supports pagination and sorting for all list endpoints. Use the `/paginated` endpoints for this functionality:

```bash
# Get paginated videos with sorting
curl -X GET "http://localhost:8080/videos/paginated?page=0&size=10&sort=uploadDate&direction=DESC" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

Parameters:
- `page`: Page number (0-based)
- `size`: Number of items per page
- `sort`: Field to sort by (e.g., title, uploadDate, durationInSeconds)
- `direction`: Sort direction (ASC or DESC)

The pagination response includes only the essential pagination information:
```json
{
  "content": [
    {
      "id": 1,
      "title": "Introduction to Spring Boot",
      "description": "Learn the basics of Spring Boot framework",
      "url": "https://youtube.com/watch?v=abc123",
      "source": "YouTube",
      "uploadDate": "2023-01-15",
      "durationInSeconds": 600,
      "createdAt": "2023-01-15 10:30:00"
    },
    {
      "id": 2,
      "title": "Advanced Java Programming",
      "description": "Advanced techniques for Java developers",
      "url": "https://youtube.com/watch?v=def456",
      "source": "YouTube",
      "uploadDate": "2023-02-20",
      "durationInSeconds": 900,
      "createdAt": "2023-02-20 14:45:00"
    }
  ],
  "page": 0,
  "size": 10,
  "sort": "uploadDate",
  "direction": "DESC",
  "totalElements": 42
}
```

### Caching for Statistics

The application uses Caffeine caching to improve performance for the statistics endpoint. Statistics are cached for 5 minutes, which means:

- The first request to `/videos/stats` will query the database and cache the results
- Subsequent requests within 5 minutes will return the cached results without querying the database
- After 5 minutes, the cache expires and the next request will query the database again
- Any video creation or import operation will invalidate the cache

This significantly improves performance for frequently accessed statistics, especially as the number of videos grows.

### Asynchronous Video Imports

For large imports, you can use the asynchronous import feature:

```bash
# Create an import job
curl -X POST "http://localhost:8080/import-jobs?source=YouTube&count=10" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."

# Check import job status
curl -X GET "http://localhost:8080/import-jobs/1" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."

# Get all import jobs
curl -X GET "http://localhost:8080/import-jobs" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."

# Get paginated import jobs with filtering
curl -X GET "http://localhost:8080/import-jobs/paginated?status=COMPLETED&page=0&size=10&sort=createdAt&direction=DESC" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### Docker Deployment

The application can be easily deployed using Docker:

```bash
# Build and start the application
docker-compose up -d

# Stop the application
docker-compose down
```

Make sure you have Docker and Docker Compose installed on your system.

## Testing with Postman

Postman is a popular API client that makes it easy to test and interact with your APIs. Here's how to test the Video Metadata Service endpoints using Postman:

### Setting Up Postman

1. Download and install Postman from [postman.com](https://www.postman.com/downloads/)
2. Create a new Collection by clicking the "+" button next to "Collections" in the sidebar
3. Name your collection "Video Metadata Service"

### Authentication in Postman

1. Create a new request by clicking the "+" button in the tabs area
2. Set up the login request:
   - Method: POST
   - URL: http://localhost:8080/auth/login
   - Headers: Add a header with Key "Content-Type" and Value "application/json"
   - Body: Select "raw" and "JSON", then enter:
     ```json
     {
       "username": "admin",
       "password": "admin123"
     }
     ```
3. Send the request by clicking the "Send" button
4. You should receive a response with a JWT token:
   ```json
   {
     "token": "eyJhbGciOiJIUzI1NiJ9...",
     "username": "admin",
     "roles": ["ADMIN", "USER"]
   }
   ```
5. Copy the token value (without quotes)

### Using the JWT Token for Authenticated Requests

For all subsequent requests, you need to include the JWT token in the Authorization header:

1. Create a new request
2. Go to the "Authorization" tab
3. Select "Bearer Token" from the Type dropdown
4. Paste your JWT token in the "Token" field
5. Now your request is authenticated and ready to be sent

### Testing Endpoints

#### Get All Videos

1. Create a new request:
   - Method: GET
   - URL: http://localhost:8080/videos
   - Authorization: Bearer Token (as set up above)
2. Send the request

#### Get Videos by Source

1. Create a new request:
   - Method: GET
   - URL: http://localhost:8080/videos?source=YouTube
   - Authorization: Bearer Token
2. Send the request

#### Get Video by ID

1. Create a new request:
   - Method: GET
   - URL: http://localhost:8080/videos/1 (replace "1" with an actual video ID)
   - Authorization: Bearer Token
2. Send the request

#### Get Video Statistics

1. Create a new request:
   - Method: GET
   - URL: http://localhost:8080/videos/stats
   - Authorization: Bearer Token
2. Send the request

#### Create a Video (Admin only)

1. Create a new request:
   - Method: POST
   - URL: http://localhost:8080/videos/create
   - Headers: Add a header with Key "Content-Type" and Value "application/json"
   - Authorization: Bearer Token (make sure you're using the admin token)
   - Body: Select "raw" and "JSON", then enter:
     ```json
     {
       "title": "My New Video",
       "description": "This is a description of my video",
       "url": "https://example.com/video123",
       "source": "YouTube",
       "uploadDate": "2023-01-15",
       "durationInSeconds": 600
     }
     ```
2. Send the request

#### Import Videos (Admin only)

1. Create a new request:
   - Method: POST
   - URL: http://localhost:8080/videos/import?count=5
   - Authorization: Bearer Token (make sure you're using the admin token)
2. Send the request

### Using Environment Variables (Optional)

For more efficient testing, you can set up environment variables in Postman:

1. Click the "Environment" dropdown in the top right and select "New"
2. Name your environment "Video Metadata Service"
3. Add a variable named "baseUrl" with value "http://localhost:8080"
4. Add a variable named "token" (leave it empty for now)
5. Save the environment
6. After authenticating, use the "Tests" tab in your login request to automatically set the token:
   ```javascript
   var jsonData = JSON.parse(responseBody);
   pm.environment.set("token", jsonData.token);
   ```
7. In subsequent requests, you can use:
   - URL: {{baseUrl}}/videos
   - Authorization: Bearer Token with value {{token}}

This approach allows you to run multiple requests without manually copying the token each time.

## Design Decisions and Assumptions

### Authentication

- JWT-based authentication was chosen for its stateless nature, which is ideal for RESTful APIs.
- Tokens expire after 24 hours by default (configurable).
- Passwords are stored using BCrypt hashing.

### Data Model

- Videos are stored with metadata including title, description, URL, source, upload date, and duration.
- The system supports multiple video sources (YouTube, Vimeo, Internal).

### Architecture

- The application follows a layered architecture:
  - **Domain**: Entity classes
  - **Repository**: Data access layer
  - **Service**: Business logic
  - **Web**: Controllers and DTOs
  - **Config**: Configuration classes
  - **Security**: Authentication and authorization

### External API Integration

- The application includes a mock external service that simulates fetching video metadata from external sources.
- In a production environment, this would be replaced with actual API clients for YouTube, Vimeo, etc.

### Assumptions

- The application assumes that video metadata is relatively static and doesn't need real-time updates.
- The application is designed for a moderate load and doesn't include advanced caching or performance optimizations.
- The in-memory H2 database is used for simplicity; in a production environment, a persistent database would be used.
