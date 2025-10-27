package com.example.authtest.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import com.example.authtest.security.CookieUtils;
import com.example.authtest.security.JwtService;
import com.example.authtest.user.UserRepository;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository users;
    private final String frontendUrl;
    private final long accessMinutes;

    public OAuth2LoginSuccessHandler(JwtService jwtService,
                                     UserRepository users,
                                     @Value("${app.frontend-url}") String frontendUrl,
                                     @Value("${app.jwt.access-token-minutes}") long accessMinutes) {
        this.jwtService = jwtService;
        this.users = users;
        this.frontendUrl = frontendUrl;
        this.accessMinutes = accessMinutes;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OidcUser oidc = (OidcUser) authentication.getPrincipal();
        String email = oidc.getEmail();

        var user = users.findByEmail(email).orElseThrow();
        String subject = "user:" + user.getId();

        String token = jwtService.issue(authentication, subject, email);
        boolean secure = request.isSecure(); // false on http://localhost
        CookieUtils.addAccessTokenCookie(response, token, (int) (accessMinutes * 60), secure);

        response.sendRedirect(frontendUrl); // SPA will call /api/auth/me to load profile
    }
}