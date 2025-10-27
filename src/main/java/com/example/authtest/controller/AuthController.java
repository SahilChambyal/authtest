package com.example.authtest.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.example.authtest.dto.AuthResponse;
import com.example.authtest.dto.LoginRequest;
import com.example.authtest.dto.RegisterRequest;
import com.example.authtest.security.CookieUtils;
import com.example.authtest.security.JwtService;
import com.example.authtest.user.AuthProvider;
import com.example.authtest.user.User;
import com.example.authtest.user.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final long accessMinutes;
    private final String issuer;

    public AuthController(UserRepository users,
                          PasswordEncoder encoder,
                          AuthenticationManager authManager,
                          JwtService jwtService,
                          @Value("${app.jwt.access-token-minutes}") long accessMinutes,
                          @Value("${app.jwt.issuer}") String issuer) {
        this.users = users;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.accessMinutes = accessMinutes;
        this.issuer = issuer;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (users.existsByEmail(req.email)) {
            return ResponseEntity.badRequest().body(Map.of("error", "email_taken"));
        }
        User u = new User();
        u.setEmail(req.email);
        u.setName(req.name);
        u.setPasswordHash(encoder.encode(req.password));
        u.setProvider(AuthProvider.LOCAL);
        u.setRoles("ROLE_USER");
        users.save(u);
        return ResponseEntity.created(URI.create("/api/auth/me")).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req,
                                              HttpServletRequest request,
                                              HttpServletResponse response) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email, req.password));

        var user = users.findByEmail(req.email).orElseThrow();
        String subject = "user:" + user.getId();
        String token = jwtService.issue(auth, subject, user.getEmail());
        boolean secure = request.isSecure();

        CookieUtils.addAccessTokenCookie(response, token, (int) (accessMinutes * 60), secure);
        return ResponseEntity.ok(new AuthResponse(token, accessMinutes * 60));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        boolean secure = request.isSecure();
        CookieUtils.clearAccessTokenCookie(response, secure);
        return ResponseEntity.noContent().build();
    }

    // Uses Jwt Authentication provided by resource server
    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(Map.of(
                "sub", jwt.getSubject(),
                "email", jwt.getClaimAsString("email"),
                "scope", jwt.getClaimAsString("scope"),
                "iat", jwt.getIssuedAt() != null ? jwt.getIssuedAt().toString() : null,
                "exp", jwt.getExpiresAt() != null ? jwt.getExpiresAt().toString() : null,
                "issuer", issuer,
                "serverTime", Instant.now().toString()
        ));
    }
}