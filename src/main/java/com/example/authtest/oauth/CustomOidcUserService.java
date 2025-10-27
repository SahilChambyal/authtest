package com.example.authtest.oauth;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.authtest.user.AuthProvider;
import com.example.authtest.user.User;
import com.example.authtest.user.UserRepository;

@Service
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository users;

    public CustomOidcUserService(UserRepository users) {
        this.users = users;
    }

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);
        String email = oidcUser.getEmail();
        String sub = oidcUser.getSubject();
        String name = oidcUser.getFullName() != null ? oidcUser.getFullName() : email;

        users.findByEmail(email).orElseGet(() -> {
            User u = new User();
            u.setEmail(email);
            u.setName(name);
            u.setProvider(AuthProvider.GOOGLE);
            u.setProviderId(sub);
            u.setRoles("ROLE_USER");
            return users.save(u);
        });
        return oidcUser;
    }
}