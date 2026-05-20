package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecipeMongoRepository extends MongoRepository<RecipeDocument, String> {
    List<RecipeDocument> findByCategory(String category);
    List<RecipeDocument> findByStatus(String status);

    @Query("{ 'ingredientsList.productId': ?0 }")
    List<RecipeDocument> findByIngredientProductId(String productId);
}
