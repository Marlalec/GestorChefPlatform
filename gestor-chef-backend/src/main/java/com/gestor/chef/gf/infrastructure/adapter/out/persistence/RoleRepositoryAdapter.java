package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import com.gestor.chef.gf.domain.model.Role;
import com.gestor.chef.gf.domain.port.out.RoleRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepositoryPort {
    private final RoleMongoRepository mongoRepository;

    @Override public Role save(Role r) { return mongoRepository.save(RoleDocument.fromDomain(r)).toDomain(); }
    @Override public Optional<Role> findById(String id) { return mongoRepository.findById(id).map(RoleDocument::toDomain); }
    @Override public Optional<Role> findByName(String n) { return mongoRepository.findByName(n).map(RoleDocument::toDomain); }
    @Override public List<Role> findAll() { return mongoRepository.findAll().stream().map(RoleDocument::toDomain).toList(); }
    @Override public void deleteById(String id) { mongoRepository.deleteById(id); }
}
