package com.gestor.chef.gf.application.service;

import com.gestor.chef.gf.domain.model.User;
import com.gestor.chef.gf.domain.model.value.DomainValues;
import com.gestor.chef.gf.domain.port.in.AuthUseCase;
import com.gestor.chef.gf.domain.port.in.UserUseCase;
import com.gestor.chef.gf.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static com.gestor.chef.gf.application.service.support.DomainValidator.requireAllowed;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserUseCase userUseCase;

    @Override
    public String login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        return jwtTokenProvider.generateToken(authentication);
    }

    @Override
    public User register(User user) {
        user.setRol(requireAllowed(user.getRol(), DomainValues.Role.PUBLIC_REGISTRATION, "rol"));
        return userUseCase.createUser(user);
    }

    @Override
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    @Override
    public String refreshToken(String token) {
        return jwtTokenProvider.refreshToken(token);
    }
}
