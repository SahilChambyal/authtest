# AuthTest - Spring Boot Authentication Demo

A comprehensive authentication system built with Spring Boot 3.5.7 and Java 21, demonstrating both traditional email/password authentication and OAuth2 (Google) login with JWT-based session management.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
- [Security Implementation](#security-implementation)
- [Database Schema](#database-schema)
- [Docker Deployment](#docker-deployment)
- [Frontend Integration](#frontend-integration)
- [Development](#development)
- [Testing](#testing)

## 🎯 Overview

AuthTest is a production-ready authentication service that provides:
- **Local Authentication**: Traditional email/password registration and login
- **OAuth2 Integration**: Google OAuth2/OpenID Connect authentication
- **JWT-based Sessions**: Stateless authentication using JSON Web Tokens
- **Cookie Management**: Secure, HTTP-only cookie storage for JWTs
- **CORS Support**: Cross-origin resource sharing for SPA frontends
- **H2 Database**: In-memory database for development and testing

## ✨ Features

### Authentication Methods
- ✅ Email/Password Registration
- ✅ Email/Password Login
- ✅ Google OAuth2 Login (OpenID Connect)
- ✅ Secure Logout

### Security Features
- 🔒 BCrypt password hashing
- 🔑 JWT token generation and validation
- 🍪 HTTP-only cookie storage
- 🛡️ CSRF protection disabled (for stateless REST API)
- 🌐 CORS configuration for frontend integration
- 🔐 OAuth2 Resource Server with JWT validation
- 👤 User roles and authorities management

### Additional Features
- 📊 H2 Console for database inspection
- 🐳 Multi-stage Dockerfile for optimized builds
- 🎨 Sample React frontend (static resources)
- 🔄 Stateless session management
- 📝 Bean Validation for request DTOs

## 🏗️ Architecture

### High-Level Flow

```
┌─────────────┐         ┌──────────────────┐         ┌──────────────┐
│   Frontend  │────────▶│  Spring Boot API │────────▶│  H2 Database │
│  (React)    │◀────────│  (AuthTest)      │◀────────│   (Memory)   │
└─────────────┘         └──────────────────┘         └──────────────┘
      │                         │
      │                         │
      └─────────────────────────┘
            JWT in Cookie
```

### Authentication Flow

#### Local Authentication (Email/Password)
1. User registers with email, password, and name
2. Password is hashed using BCrypt
3. User credentials stored in database
4. User logs in with email/password
5. System validates credentials
6. JWT token is generated and stored in HTTP-only cookie
7. Client includes cookie in subsequent requests
8. JWT is validated for protected endpoints

#### OAuth2 Authentication (Google)
1. User clicks "Continue with Google"
2. Redirected to Google OAuth2 authorization
3. User authorizes the application
4. Google redirects back with authorization code
5. Spring Security exchanges code for tokens
6. User info is fetched from Google
7. User is created/updated in database
8. JWT token is generated and stored in cookie
9. User is redirected to frontend

## 🛠️ Technologies

### Backend
- **Java 21** - Modern Java LTS version
- **Spring Boot 3.5.7** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database access and ORM
- **Spring OAuth2 Client** - OAuth2/OIDC integration
- **Spring OAuth2 Resource Server** - JWT validation
- **Hibernate** - JPA implementation
- **H2 Database** - In-memory database

### Build & Deployment
- **Gradle 8.x** - Build automation
- **Docker** - Containerization
- **Eclipse Temurin** - OpenJDK distribution

### Frontend (Sample)
- **React** - UI library
- **JavaScript (ES6+)** - Programming language

## 📁 Project Structure

```
authtest/
├── src/
│   ├── main/
│   │   ├── java/com/example/authtest/
│   │   │   ├── AuthtestApplication.java          # Main application class
│   │   │   ├── config/
│   │   │   │   ├── Auth.java                     # Auth configuration annotations
│   │   │   │   ├── CorsConfig.java               # CORS configuration
│   │   │   │   ├── JwtBeans.java                 # JWT encoder/decoder beans
│   │   │   │   └── SecurityConfig.java           # Security filter chain
│   │   │   ├── controller/
│   │   │   │   └── AuthController.java           # Authentication endpoints
│   │   │   ├── dto/
│   │   │   │   ├── AuthResponse.java             # Login response DTO
│   │   │   │   ├── LoginRequest.java             # Login request DTO
│   │   │   │   └── RegisterRequest.java          # Registration request DTO
│   │   │   ├── oauth/
│   │   │   │   ├── CustomOidcUserService.java    # OIDC user handler
│   │   │   │   └── OAuth2LoginSuccessHandler.java # OAuth2 success handler
│   │   │   ├── security/
│   │   │   │   ├── CookieBearerTokenResolver.java # JWT from cookie resolver
│   │   │   │   ├── CookieUtils.java              # Cookie management utilities
│   │   │   │   ├── JwtService.java               # JWT generation service
│   │   │   │   └── RestAuthEntryPoint.java       # Custom auth entry point
│   │   │   └── user/
│   │   │       ├── AuthProvider.java             # Enum: LOCAL, GOOGLE
│   │   │       ├── User.java                     # User entity
│   │   │       └── UserRepository.java           # User data access
│   │   └── resources/
│   │       ├── application.yml                    # Application configuration
│   │       └── static/                            # Static frontend resources
│   │           ├── app.js                         # React app
│   │           └── api.js                         # API client
│   └── test/
│       └── java/com/example/authtest/
│           └── AuthtestApplicationTests.java      # Test suite
├── build.gradle                                    # Gradle build configuration
├── settings.gradle                                 # Gradle settings
├── gradlew & gradlew.bat                          # Gradle wrapper scripts
├── Dockerfile                                      # Multi-stage Docker build
└── README.md                                       # This file
```

## 🚀 Getting Started

### Prerequisites

- **Java 21** or higher
- **Gradle 8.x** (or use included wrapper)
- **Google OAuth2 Credentials** (for OAuth login)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Lucifer3377/authtest.git
   cd authtest
   ```

2. **Set up environment variables**
   
   Create a `.env` file or set environment variables:
   ```bash
   GOOGLE_CLIENT_ID=your-google-client-id
   GOOGLE_CLIENT_SECRET=your-google-client-secret
   ```

   > **Note**: Get Google OAuth2 credentials from [Google Cloud Console](https://console.cloud.google.com/)
   > - Create a new project
   > - Enable Google+ API
   > - Create OAuth2 credentials
   > - Add `http://localhost:8080/login/oauth2/code/google` as redirect URI

3. **Build the project**
   ```bash
   # Windows
   gradlew.bat build
   
   # Linux/Mac
   ./gradlew build
   ```

4. **Run the application**
   ```bash
   # Windows
   gradlew.bat bootRun
   
   # Linux/Mac
   ./gradlew bootRun
   ```

5. **Access the application**
   - Backend API: http://localhost:8080
   - H2 Console: http://localhost:8080/h2-console
   - Frontend: http://localhost:8080/ (if serving static files)

## ⚙️ Configuration

### application.yml

```yaml
server:
  port: 8080                              # Server port

spring:
  datasource:
    url: jdbc:h2:mem:authdb               # H2 in-memory database
    username: sa
    password:
  
  jpa:
    hibernate:
      ddl-auto: update                    # Auto-update schema
    
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope: [openid, profile, email]

app:
  frontend-url: http://localhost:5173     # Frontend redirect URL
  jwt:
    issuer: demo-auth                     # JWT issuer
    access-token-minutes: 120             # JWT expiry (2 hours)
```

### Key Configuration Properties

| Property | Description | Default |
|----------|-------------|---------|
| `server.port` | Application port | 8080 |
| `app.frontend-url` | Frontend URL for OAuth redirect | http://localhost:5173 |
| `app.jwt.issuer` | JWT issuer name | demo-auth |
| `app.jwt.access-token-minutes` | JWT expiration time | 120 (2 hours) |
| `GOOGLE_CLIENT_ID` | Google OAuth2 client ID | (required) |
| `GOOGLE_CLIENT_SECRET` | Google OAuth2 client secret | (required) |

## 📡 API Endpoints

### Public Endpoints

#### Register New User
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "securePassword123"
}
```

**Response:**
- `201 Created` - User created successfully
- `400 Bad Request` - Email already exists or validation error

---

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJSUzI1NiJ9...",
  "expiresIn": 7200
}
```

Sets HTTP-only cookie: `access_token`

---

#### Logout
```http
POST /api/auth/logout
```

**Response:**
- `204 No Content` - Successfully logged out
- Clears `access_token` cookie

---

#### OAuth2 Login
```http
GET /oauth2/authorization/google
```

Redirects to Google OAuth2 authorization page. After successful authentication, redirects to `app.frontend-url` with JWT cookie set.

---

### Protected Endpoints

#### Get Current User
```http
GET /api/auth/me
Cookie: access_token=<jwt>
```

**Response:**
```json
{
  "sub": "user:123",
  "email": "john@example.com",
  "scope": "ROLE_USER",
  "iat": "2025-10-28T10:00:00Z",
  "exp": "2025-10-28T12:00:00Z",
  "issuer": "demo-auth",
  "serverTime": "2025-10-28T10:30:00Z"
}
```

---

### Development Endpoints

#### H2 Console
```http
GET /h2-console
```

Access the H2 database console for debugging.

**JDBC URL:** `jdbc:h2:mem:authdb`  
**Username:** `sa`  
**Password:** (empty)

---

## 🔐 Security Implementation

### 1. Password Encoding

Passwords are hashed using **BCrypt** with salt:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

### 2. JWT Generation

JWTs are generated with:
- **RSA-256** signing algorithm
- Custom claims: `email`, `scope`
- Configurable expiration time
- Issuer identification

```java
public String issue(Authentication auth, String subject, String email) {
    var claims = JwtClaimsSet.builder()
        .issuer(issuer)
        .issuedAt(now)
        .expiresAt(expiresAt)
        .subject(subject)
        .claim("scope", scope)
        .claim("email", email)
        .build();
    return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
}
```

### 3. Cookie Security

JWTs are stored in secure cookies with:
- **HTTP-Only**: Prevents XSS attacks
- **SameSite=Lax**: CSRF protection
- **Secure flag**: HTTPS only (in production)
- **Max-Age**: Configurable expiration

```java
public static void addAccessTokenCookie(HttpServletResponse response, 
                                       String token, 
                                       int maxAgeSeconds, 
                                       boolean secure) {
    Cookie cookie = new Cookie("access_token", token);
    cookie.setHttpOnly(true);
    cookie.setSecure(secure);
    cookie.setPath("/");
    cookie.setMaxAge(maxAgeSeconds);
    response.addCookie(cookie);
}
```

### 4. OAuth2 Integration

Google OAuth2 integration includes:
- **OIDC Discovery**: Automatic configuration from Google
- **Custom User Service**: Maps OAuth2 user to local user
- **Success Handler**: Generates JWT after OAuth2 login
- **Automatic User Creation**: Creates user on first login

### 5. Security Filter Chain

```java
http
    .cors(Customizer.withDefaults())
    .csrf(csrf -> csrf.disable())  // Stateless API
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/auth/**").permitAll()
        .requestMatchers("/oauth2/**").permitAll()
        .anyRequest().authenticated()
    )
    .oauth2Login(...)  // OAuth2 configuration
    .oauth2ResourceServer(oauth2 -> oauth2
        .bearerTokenResolver(new CookieBearerTokenResolver())
        .jwt(jwt -> jwt.decoder(jwtDecoder))
    )
    .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS));
```

## 💾 Database Schema

### User Entity

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255),        -- NULL for OAuth users
    provider VARCHAR(50) NOT NULL,     -- LOCAL or GOOGLE
    provider_id VARCHAR(255),          -- OAuth provider user ID
    roles VARCHAR(255),                -- Comma-separated roles
    created_at TIMESTAMP DEFAULT NOW()
);
```

### AuthProvider Enum

```java
public enum AuthProvider {
    LOCAL,   // Email/password authentication
    GOOGLE   // Google OAuth2 authentication
}
```

### User Fields

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Auto-generated primary key |
| `email` | String | Unique user email (username) |
| `name` | String | User's display name |
| `passwordHash` | String | BCrypt hashed password (null for OAuth) |
| `provider` | AuthProvider | Authentication provider (LOCAL/GOOGLE) |
| `providerId` | String | OAuth provider's user ID |
| `roles` | String | Space-separated roles (e.g., "ROLE_USER") |
| `createdAt` | Instant | Account creation timestamp |

## 🐳 Docker Deployment

### Multi-Stage Dockerfile

The included Dockerfile uses multi-stage builds for optimization:

1. **Builder Stage**: Compiles Java code with Gradle
2. **Layer Extraction**: Extracts Spring Boot JAR into layers
3. **Runtime Stage**: Minimal JRE image with application layers

### Build Docker Image

```bash
docker build -t authtest:latest .
```

### Run Docker Container

```bash
docker run -d \
  -p 8080:8080 \
  -e GOOGLE_CLIENT_ID=your-client-id \
  -e GOOGLE_CLIENT_SECRET=your-client-secret \
  --name authtest \
  authtest:latest
```

### Docker Compose (Optional)

Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  authtest:
    build: .
    ports:
      - "8080:8080"
    environment:
      - GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID}
      - GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET}
      - SPRING_PROFILES_ACTIVE=prod
    restart: unless-stopped
```

Run with:
```bash
docker-compose up -d
```

## 🎨 Frontend Integration

### Sample React Integration

The project includes a sample React frontend in `src/main/resources/static/`:

```javascript
// Login with email/password
const response = await fetch('/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  credentials: 'include',  // Important: include cookies
  body: JSON.stringify({ email, password })
});

// Get current user
const userResponse = await fetch('/api/auth/me', {
  credentials: 'include'  // Sends JWT cookie
});

// Logout
await fetch('/api/auth/logout', {
  method: 'POST',
  credentials: 'include'
});

// Google OAuth login
window.location.href = '/oauth2/authorization/google';
```

### CORS Configuration

For separate frontend applications:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(frontendUrl)
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(true);  // Required for cookies
    }
}
```

## 💻 Development

### Running in Development Mode

```bash
# Run with auto-reload
./gradlew bootRun --continuous

# Or use Spring DevTools (included in dependencies)
./gradlew bootRun
```

### Accessing H2 Console

1. Navigate to http://localhost:8080/h2-console
2. Use JDBC URL: `jdbc:h2:mem:authdb`
3. Username: `sa`
4. Password: (leave empty)

### Testing OAuth2 Locally

For local OAuth2 testing:
1. Add `http://localhost:8080/login/oauth2/code/google` to Google Console
2. Ensure `app.frontend-url` points to your local frontend
3. Test the flow: Frontend → `/oauth2/authorization/google` → Google → Callback → Frontend

### Debugging

Enable debug logging in `application.yml`:

```yaml
logging:
  level:
    org.springframework.security: DEBUG
    com.example.authtest: DEBUG
```

## 🧪 Testing

### Running Tests

```bash
# Run all tests
./gradlew test

# Run with coverage
./gradlew test jacocoTestReport

# Run specific test
./gradlew test --tests AuthtestApplicationTests
```

### Test Structure

```
src/test/java/com/example/authtest/
└── AuthtestApplicationTests.java    # Integration tests
```

### Manual API Testing with cURL

**Register:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com","password":"password123"}'
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{"email":"test@example.com","password":"password123"}'
```

**Get User Info:**
```bash
curl -X GET http://localhost:8080/api/auth/me \
  -b cookies.txt
```

**Logout:**
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -b cookies.txt \
  -c cookies.txt
```

## 📝 Key Classes Reference

### Core Components

| Class | Package | Description |
|-------|---------|-------------|
| `AuthtestApplication` | root | Main Spring Boot application |
| `SecurityConfig` | config | Security filter chain configuration |
| `JwtBeans` | config | JWT encoder/decoder bean definitions |
| `CorsConfig` | config | CORS policy configuration |
| `AuthController` | controller | REST endpoints for authentication |
| `JwtService` | security | JWT generation and signing |
| `CookieBearerTokenResolver` | security | Extracts JWT from cookies |
| `CookieUtils` | security | Cookie management utilities |
| `User` | user | JPA entity for users |
| `UserRepository` | user | Spring Data JPA repository |
| `CustomOidcUserService` | oauth | Handles OIDC user info |
| `OAuth2LoginSuccessHandler` | oauth | Post-OAuth login processing |

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is for demonstration purposes. Feel free to use it as a starting point for your own projects.

## 🐛 Known Issues & Limitations

- H2 database is in-memory (data lost on restart)
- No refresh token mechanism
- No email verification
- No password reset functionality
- Single OAuth provider (Google only)
- No rate limiting on authentication endpoints

## 🚀 Future Enhancements

- [ ] Persistent database (PostgreSQL/MySQL)
- [ ] Refresh token support
- [ ] Email verification workflow
- [ ] Password reset functionality
- [ ] Additional OAuth providers (GitHub, Facebook)
- [ ] Rate limiting and brute-force protection
- [ ] User profile management endpoints
- [ ] Role-based access control (RBAC) examples
- [ ] API documentation with Swagger/OpenAPI
- [ ] Actuator endpoints for monitoring

## 📞 Support

For issues, questions, or contributions:
- Open an issue on GitHub
- Contact: [Repository Owner](https://github.com/Lucifer3377)

## 🙏 Acknowledgments

- Spring Boot Team for excellent framework
- Spring Security Team for robust security features
- Google for OAuth2/OIDC implementation

---

**Built with ❤️ using Spring Boot and Java 21**
