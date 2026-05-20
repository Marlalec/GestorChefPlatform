package com.gestor.chef.gf.domain.port.in;

import com.gestor.chef.gf.domain.model.User;

public interface AuthUseCase {
    String login(String email, String password);
    User register(User user);
    boolean validateToken(String token);
    String refreshToken(String token);
}
