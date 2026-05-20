package com.gestor.chef.gf.application.service;

import com.gestor.chef.gf.application.service.support.EntityFinder;
import com.gestor.chef.gf.domain.model.User;
import com.gestor.chef.gf.domain.model.value.DomainValues;
import com.gestor.chef.gf.domain.port.in.UserUseCase;
import com.gestor.chef.gf.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.gestor.chef.gf.application.service.support.DomainValidator.requireAllowed;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(User user) {
        validateNewUser(user);
        user.setEmail(user.getEmail().trim().toLowerCase());
        user.setRol(requireAllowed(user.getRol(), DomainValues.Role.ALL, "rol"));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAccountStatus(DomainValues.Status.ACTIVE);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        User saved = userRepository.save(user);
        log.info("Usuario creado con id: {}", saved.getId());
        return saved;
    }

    @Override
    public User updateUser(String id, User user) {
        User existing = EntityFinder.required(userRepository.findById(id), "Usuario", id);
        if (user.getName() != null) existing.setName(user.getName());
        if (user.getEmail() != null) existing.setEmail(user.getEmail().trim().toLowerCase());
        if (user.getPhone() != null) existing.setPhone(user.getPhone());
        if (user.getRol() != null) existing.setRol(requireAllowed(user.getRol(), DomainValues.Role.ALL, "rol"));
        existing.setUpdatedAt(Instant.now());
        return userRepository.save(existing);
    }

    @Override
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findByAccountStatus(DomainValues.Status.ACTIVE);
    }

    @Override
    public List<User> getUsersByRole(String rol) {
        return userRepository.findByRol(requireAllowed(rol, DomainValues.Role.ALL, "rol"));
    }

    @Override
    public void deleteUser(String id) {
        User user = EntityFinder.required(userRepository.findById(id), "Usuario", id);
        user.setAccountStatus(DomainValues.Status.INACTIVE);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
    }

    @Override
    public User changeAccountStatus(String id, String status) {
        User user = EntityFinder.required(userRepository.findById(id), "Usuario", id);
        user.setAccountStatus(requireAllowed(status, DomainValues.Status.ACCOUNT, "status"));
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    @Override
    public User changePassword(String id, String newPassword) {
        User user = EntityFinder.required(userRepository.findById(id), "Usuario", id);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    private void validateNewUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        if (userRepository.existsByEmail(user.getEmail().trim().toLowerCase())) {
            throw new IllegalArgumentException("El email ya está registrado: " + user.getEmail());
        }
    }
}
