package com.gestor.chef.gf.infrastructure.adapter.in.web;

import com.gestor.chef.gf.domain.model.Recipe;
import com.gestor.chef.gf.domain.model.RecipeIngredient;
import com.gestor.chef.gf.domain.port.in.RecipeUseCase;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.ApiResponse;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.CreateRecipeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
@Tag(name = "Recetas", description = "Gestión del recetario — platos con ingredientes y costos")
public class RecipeController {

    private final RecipeUseCase recipeUseCase;

    @GetMapping
    @Operation(summary = "Listar todas las recetas (paginado). Parámetros: page, size, sort")
    public ResponseEntity<ApiResponse<Page<Recipe>>> getAll(
            @PageableDefault(size = 20, sort = "dishName") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(recipeUseCase.getAllRecipesPaged(pageable)));
    }

    @GetMapping("/active")
    @Operation(summary = "Recetas activas")
    public ResponseEntity<ApiResponse<List<Recipe>>> getActive() {
        return ResponseEntity.ok(ApiResponse.ok(recipeUseCase.getActiveRecipes()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Recipe>> getById(@PathVariable String id) {
        return recipeUseCase.getRecipeById(id)
                .map(r -> ResponseEntity.ok(ApiResponse.ok(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear receta con lista de ingredientes")
    public ResponseEntity<ApiResponse<Recipe>> create(@Valid @RequestBody CreateRecipeRequest req) {
        Recipe recipe = toRecipe(req);
        return ResponseEntity.status(201).body(ApiResponse.ok("Receta creada", recipeUseCase.createRecipe(recipe)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Recipe>> update(@PathVariable String id,
                                                       @Valid @RequestBody CreateRecipeRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(recipeUseCase.updateRecipe(id, toRecipe(req))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        recipeUseCase.deleteRecipe(id);
        return ResponseEntity.ok(ApiResponse.ok("Receta eliminada", null));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Recetas por categoría: ENTRADA | PLATO_PRINCIPAL | POSTRE | BEBIDA")
    public ResponseEntity<ApiResponse<List<Recipe>>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(ApiResponse.ok(recipeUseCase.getRecipesByCategory(category)));
    }

    @GetMapping("/ingredient/{productId}")
    @Operation(summary = "Recetas que usan un ingrediente específico")
    public ResponseEntity<ApiResponse<List<Recipe>>> searchByIngredient(@PathVariable String productId) {
        return ResponseEntity.ok(ApiResponse.ok(recipeUseCase.searchRecipesByIngredient(productId)));
    }

    @GetMapping("/{id}/availability")
    @Operation(summary = "Verificar stock para N porciones")
    public ResponseEntity<ApiResponse<Boolean>> checkAvailability(
            @PathVariable String id, @RequestParam(defaultValue = "1") int portions) {
        return ResponseEntity.ok(ApiResponse.ok(recipeUseCase.checkIngredientsAvailability(id, portions)));
    }

    @GetMapping("/{id}/cost")
    @Operation(summary = "Calcular costo real de la receta con precios actuales de ingredientes")
    public ResponseEntity<ApiResponse<BigDecimal>> calculateCost(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(recipeUseCase.calculateRecipeCost(id)));
    }

    @PostMapping("/{id}/discount-inventory")
    @Operation(summary = "Descontar ingredientes del inventario al preparar")
    public ResponseEntity<ApiResponse<Void>> discountIngredients(
            @PathVariable String id, @RequestBody Map<String, Object> body) {
        int portions = Integer.parseInt(body.getOrDefault("portions", 1).toString());
        String userId = (String) body.getOrDefault("userId", "system");
        recipeUseCase.discountIngredientsFromInventory(id, portions, userId);
        return ResponseEntity.ok(ApiResponse.ok("Ingredientes descontados del inventario", null));
    }

    private Recipe toRecipe(CreateRecipeRequest req) {
        List<RecipeIngredient> ingredients = req.getIngredientsList() == null ? null :
                req.getIngredientsList().stream()
                        .map(i -> RecipeIngredient.builder()
                                .productId(i.getProductId()).productName(i.getProductName())
                                .amount(i.getAmount()).unit(i.getUnit()).build())
                        .collect(Collectors.toList());
        return Recipe.builder()
                .dishName(req.getDishName()).category(req.getCategory())
                .description(req.getDescription()).ingredientsList(ingredients)
                .price(req.getPrice()).preparationTimeMinutes(req.getPreparationTimeMinutes())
                .build();
    }
}
