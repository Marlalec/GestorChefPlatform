package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import com.gestor.chef.gf.domain.model.User;
import com.gestor.chef.gf.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserMongoRepository mongoRepository;

    @Override
    public User save(User user) {
        return mongoRepository.save(UserDocument.fromDomain(user)).toDomain();
    }

    @Override
    public Optional<User> findById(String id) {
        return mongoRepository.findById(id).map(UserDocument::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return mongoRepository.findByEmail(email).map(UserDocument::toDomain);
    }

    @Override
    public List<User> findAll() {
        return mongoRepository.findAll().stream().map(UserDocument::toDomain).toList();
    }

    @Override
    public List<User> findByRol(String rol) {
        return mongoRepository.findByRol(rol).stream().map(UserDocument::toDomain).toList();
    }

    @Override
    public List<User> findByAccountStatus(String accountStatus) {
        return mongoRepository.findByAccountStatus(accountStatus).stream().map(UserDocument::toDomain).toList();
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return mongoRepository.existsByEmail(email);
    }
}
