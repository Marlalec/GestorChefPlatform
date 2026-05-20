package com.gestor.chef.gf.domain.port.out;

import com.gestor.chef.gf.domain.model.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface RecipeRepositoryPort {
    Recipe save(Recipe recipe);
    Optional<Recipe> findById(String id);
    List<Recipe> findAll();
    Page<Recipe> findAllPaged(Pageable pageable);
    List<Recipe> findByCategory(String category);
    List<Recipe> findByStatus(String status);
    List<Recipe> findByIngredientProductId(String productId);
    void deleteById(String id);
}
