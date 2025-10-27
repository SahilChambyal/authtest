package com.example.authtest.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final String issuer;
    private final long accessMinutes;

    public JwtService(JwtEncoder encoder,
                      @Value("${app.jwt.issuer}") String issuer,
                      @Value("${app.jwt.access-token-minutes}") long accessMinutes) {
        this.encoder = encoder;
        this.issuer = issuer;
        this.accessMinutes = accessMinutes;
    }

    public String issue(Authentication auth, String subject, String email) {
        var now = Instant.now();
        var expiresAt = now.plusSeconds(accessMinutes * 60);

        String scope = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(subject)
                .claim("scope", scope)
                .claim("email", email)
                .build();

        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}