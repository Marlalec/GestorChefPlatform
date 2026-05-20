package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import com.gestor.chef.gf.domain.model.Recipe;
import com.gestor.chef.gf.domain.port.out.RecipeRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecipeRepositoryAdapter implements RecipeRepositoryPort {
    private final RecipeMongoRepository mongoRepository;

    @Override public Recipe save(Recipe r) { return mongoRepository.save(RecipeDocument.fromDomain(r)).toDomain(); }
    @Override public Optional<Recipe> findById(String id) { return mongoRepository.findById(id).map(RecipeDocument::toDomain); }
    @Override public List<Recipe> findAll() { return mongoRepository.findAll().stream().map(RecipeDocument::toDomain).toList(); }
    @Override public Page<Recipe> findAllPaged(Pageable pageable) { return mongoRepository.findAll(pageable).map(RecipeDocument::toDomain); }
    @Override public List<Recipe> findByCategory(String c) { return mongoRepository.findByCategory(c).stream().map(RecipeDocument::toDomain).toList(); }
    @Override public List<Recipe> findByStatus(String s) { return mongoRepository.findByStatus(s).stream().map(RecipeDocument::toDomain).toList(); }
    @Override public List<Recipe> findByIngredientProductId(String pid) { return mongoRepository.findByIngredientProductId(pid).stream().map(RecipeDocument::toDomain).toList(); }
    @Override public void deleteById(String id) { mongoRepository.deleteById(id); }
}
