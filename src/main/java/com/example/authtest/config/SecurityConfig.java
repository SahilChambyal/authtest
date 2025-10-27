package com.example.authtest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.authtest.oauth.CustomOidcUserService;
import com.example.authtest.oauth.OAuth2LoginSuccessHandler;
import com.example.authtest.security.CookieBearerTokenResolver;
import com.example.authtest.security.RestAuthEntryPoint;
import com.example.authtest.user.UserRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Use DB-backed users for LOCAL authentication
    @Bean
    public UserDetailsService userDetailsService(UserRepository repo) {
        return username -> repo.findByEmail(username)
                .map(u -> User.withUsername(u.getEmail())
                        .password(u.getPasswordHash() != null ? u.getPasswordHash() : "{noop}")
                        .roles((u.getRoles() != null && !u.getRoles().isBlank()) ? u.getRoles().replace("ROLE_", "") : "USER")
                        .accountLocked(false)
                        .disabled(false)
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration,
                                                       UserDetailsService uds,
                                                       PasswordEncoder encoder) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(uds);
        provider.setPasswordEncoder(encoder);
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           CustomOidcUserService oidcUserService,
                                           OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
                                           JwtDecoder jwtDecoder) throws Exception {

        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(e -> e.authenticationEntryPoint(new RestAuthEntryPoint()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/error").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/oauth2/**", "/login/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
                .anyRequest().authenticated()
            )
            .headers(h -> h.frameOptions(frame -> frame.sameOrigin())) // h2-console
            .oauth2Login(oauth -> oauth
                .userInfoEndpoint(u -> u.oidcUserService(oidcUserService))
                .successHandler(oAuth2LoginSuccessHandler)
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .bearerTokenResolver(new CookieBearerTokenResolver())
                .jwt(jwt -> jwt.decoder(jwtDecoder))
            )
            .sessionManagement(sm -> sm.sessionCreationPolicy(
                    org.springframework.security.config.http.SessionCreationPolicy.STATELESS));

        // No UsernamePasswordAuthenticationFilter for form login; we expose REST endpoints instead
        return http.build();
    }
}