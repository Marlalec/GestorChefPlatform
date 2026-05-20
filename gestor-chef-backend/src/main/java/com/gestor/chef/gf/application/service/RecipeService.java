package com.gestor.chef.gf.application.service;

import com.gestor.chef.gf.application.service.support.EntityFinder;
import com.gestor.chef.gf.domain.model.InventoryMovement;
import com.gestor.chef.gf.domain.model.Recipe;
import com.gestor.chef.gf.domain.model.value.DomainValues;
import com.gestor.chef.gf.domain.port.in.InventoryMovementUseCase;
import com.gestor.chef.gf.domain.port.in.ProductUseCase;
import com.gestor.chef.gf.domain.port.in.RecipeUseCase;
import com.gestor.chef.gf.domain.port.out.RecipeRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecipeService implements RecipeUseCase {

    private final RecipeRepositoryPort recipeRepository;
    private final ProductUseCase productUseCase;
    private final InventoryMovementUseCase movementUseCase;

    @Override
    @Caching(evict = {
        @CacheEvict(value = "recipes", allEntries = true),
        @CacheEvict(value = "recipes_active", allEntries = true),
        @CacheEvict(value = "recipes_by_category", allEntries = true),
        @CacheEvict(value = "recipes_by_ingredient", allEntries = true)
    })
    public Recipe createRecipe(Recipe recipe) {
        Instant now = Instant.now();
        recipe.setStatus(DomainValues.Status.ACTIVE);
        recipe.setCreatedAt(now);
        recipe.setUpdatedAt(now);
        return recipeRepository.save(recipe);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "recipes", allEntries = true),
        @CacheEvict(value = "recipes_active", allEntries = true),
        @CacheEvict(value = "recipes_by_category", allEntries = true),
        @CacheEvict(value = "recipes_by_ingredient", allEntries = true)
    })
    public Recipe updateRecipe(String id, Recipe recipe) {
        Recipe existing = EntityFinder.required(recipeRepository.findById(id), "Receta", id);
        mergeRecipe(existing, recipe);
        existing.setUpdatedAt(Instant.now());
        return recipeRepository.save(existing);
    }

    @Override
    @Cacheable(value = "recipes", key = "#id")
    public Optional<Recipe> getRecipeById(String id) {
        return recipeRepository.findById(id);
    }

    @Override
    @Cacheable(value = "recipes", unless = "#result.isEmpty()")
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findByStatus(DomainValues.Status.ACTIVE);
    }

    @Override
    public Page<Recipe> getAllRecipesPaged(Pageable pageable) {
        return recipeRepository.findAllPaged(pageable);
    }

    @Override
    @Cacheable(value = "recipes_by_category", key = "#category", unless = "#result.isEmpty()")
    public List<Recipe> getRecipesByCategory(String category) {
        return recipeRepository.findByCategory(category);
    }

    @Override
    @Cacheable(value = "recipes_by_ingredient", key = "#productId", unless = "#result.isEmpty()")
    public List<Recipe> searchRecipesByIngredient(String productId) {
        return recipeRepository.findByIngredientProductId(productId);
    }

    @Override
    @Cacheable(value = "recipes_active", unless = "#result.isEmpty()")
    public List<Recipe> getActiveRecipes() {
        return recipeRepository.findByStatus(DomainValues.Status.ACTIVE);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "recipes", allEntries = true),
        @CacheEvict(value = "recipes_active", allEntries = true),
        @CacheEvict(value = "recipes_by_category", allEntries = true),
        @CacheEvict(value = "recipes_by_ingredient", allEntries = true)
    })
    public void deleteRecipe(String id) {
        Recipe recipe = EntityFinder.required(recipeRepository.findById(id), "Receta", id);
        recipe.setStatus(DomainValues.Status.INACTIVE);
        recipe.setUpdatedAt(Instant.now());
        recipeRepository.save(recipe);
    }

    @Override
    public boolean checkIngredientsAvailability(String recipeId, int portions) {
        Recipe recipe = EntityFinder.required(recipeRepository.findById(recipeId), "Receta", recipeId);
        if (recipe.getIngredientsList() == null) {
            return false;
        }
        return recipe.getIngredientsList().stream().allMatch(ingredient ->
                productUseCase.getProductById(ingredient.getProductId())
                        .map(product -> product.getQuantity() >= ingredient.getAmount() * portions)
                        .orElse(false)
        );
    }

    @Override
    public void discountIngredientsFromInventory(String recipeId, int portions, String userId) {
        Recipe recipe = EntityFinder.required(recipeRepository.findById(recipeId), "Receta", recipeId);
        if (recipe.getIngredientsList() == null) {
            return;
        }
        recipe.getIngredientsList().forEach(ingredient -> {
            InventoryMovement movement = InventoryMovement.builder()
                    .productId(ingredient.getProductId())
                    .quantityChanged(ingredient.getAmount() * portions)
                    .movementType(DomainValues.MovementType.OUT)
                    .reason(DomainValues.MovementReason.RECIPE_USE)
                    .userId(userId)
                    .notes("Receta: " + recipe.getDishName() + " x" + portions + " porciones")
                    .build();
            movementUseCase.registerMovement(movement);
        });
    }

    @Override
    public BigDecimal calculateRecipeCost(String recipeId) {
        Recipe recipe = EntityFinder.required(recipeRepository.findById(recipeId), "Receta", recipeId);
        if (recipe.getIngredientsList() == null || recipe.getIngredientsList().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return recipe.getIngredientsList().stream()
                .map(ingredient -> productUseCase.getProductById(ingredient.getProductId())
                        .map(product -> product.getPrice() != null
                                ? product.getPrice().multiply(BigDecimal.valueOf(ingredient.getAmount()))
                                : BigDecimal.ZERO)
                        .orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void mergeRecipe(Recipe existing, Recipe update) {
        if (update.getDishName() != null) existing.setDishName(update.getDishName());
        if (update.getCategory() != null) existing.setCategory(update.getCategory());
        if (update.getDescription() != null) existing.setDescription(update.getDescription());
        if (update.getIngredientsList() != null) existing.setIngredientsList(update.getIngredientsList());
        if (update.getPrice() != null) existing.setPrice(update.getPrice());
        if (update.getPreparationTimeMinutes() > 0) existing.setPreparationTimeMinutes(update.getPreparationTimeMinutes());
    }
}
