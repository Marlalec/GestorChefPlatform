package com.gestor.chef.gf.domain.port.in;

import com.gestor.chef.gf.domain.model.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RecipeUseCase {
    Recipe createRecipe(Recipe recipe);
    Recipe updateRecipe(String id, Recipe recipe);
    Optional<Recipe> getRecipeById(String id);
    List<Recipe> getAllRecipes();
    Page<Recipe> getAllRecipesPaged(Pageable pageable);
    List<Recipe> getRecipesByCategory(String category);
    List<Recipe> searchRecipesByIngredient(String productId);
    List<Recipe> getActiveRecipes();
    void deleteRecipe(String id);
    boolean checkIngredientsAvailability(String recipeId, int portions);
    void discountIngredientsFromInventory(String recipeId, int portions, String userId);

    BigDecimal calculateRecipeCost(String recipeId);
}
