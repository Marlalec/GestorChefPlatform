package com.gestor.chef.gf.domain.port.out;

import com.gestor.chef.gf.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    List<User> findByRol(String rol);
    List<User> findByAccountStatus(String accountStatus);
    void deleteById(String id);
    boolean existsByEmail(String email);
}
