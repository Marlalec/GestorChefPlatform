package com.gestor.chef.gf.infrastructure.config;

import com.gestor.chef.gf.domain.model.*;
import com.gestor.chef.gf.domain.port.in.*;
import com.gestor.chef.gf.domain.port.out.ProductRepositoryPort;
import com.gestor.chef.gf.domain.port.out.RecipeRepositoryPort;
import com.gestor.chef.gf.domain.port.out.SupplierRepositoryPort;
import com.gestor.chef.gf.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final UserRepositoryPort     userRepository;
    private final ProductRepositoryPort  productRepository;
    private final SupplierRepositoryPort supplierRepository;
    private final RecipeRepositoryPort   recipeRepository;

    private final PasswordEncoder  passwordEncoder;
    private final SupplierUseCase  supplierUseCase;
    private final ProductUseCase   productUseCase;
    private final RecipeUseCase    recipeUseCase;

    @Override
    public void run(ApplicationArguments args) {
        seedUsers();
        seedSuppliers();
        seedProducts();
        seedRecipes();
    }

    private void seedUsers() {
        if (userRepository.existsByEmail("admin@gestor.chef")) {
            log.info("[DataSeeder] Usuarios ya existen — omitiendo seed.");
            return;
        }
        log.info("[DataSeeder] Creando usuarios por defecto...");

        userRepository.save(buildUser("Administrador",  "admin@gestor.chef",    "Admin123!", "ADMIN"));
        userRepository.save(buildUser("Chef Principal", "cocina@gestor.chef",   "Cocina123!", "COCINA"));
        userRepository.save(buildUser("Contabilidad",   "contable@gestor.chef", "Contable123!", "CONTABLE"));

        log.info("[DataSeeder] ✔ 3 usuarios creados (admin / cocina / contable).");
    }

    private User buildUser(String name, String email, String rawPassword, String rol) {
        return User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .rol(rol)
                .accountStatus("ACTIVE")
                .build();
    }

    private void seedSuppliers() {
        if (!supplierRepository.findAll().isEmpty()) {
            log.info("[DataSeeder] Proveedores ya existen — omitiendo seed.");
            return;
        }
        log.info("[DataSeeder] Creando proveedores de ejemplo...");

        supplierUseCase.createSupplier(Supplier.builder()
                .name("Carnes Premium S.A.S")
                .contactName("Carlos Rodríguez")
                .email("ventas@carnespremium.com")
                .phone("+57 310 000 0001")
                .address("Cra 15 # 80-20, Bogotá")
                .build());

        supplierUseCase.createSupplier(Supplier.builder()
                .name("Verduras del Campo")
                .contactName("María López")
                .email("pedidos@verdurasdecampo.com")
                .phone("+57 311 000 0002")
                .address("Mercado Central, Local 45, Bogotá")
                .build());

        supplierUseCase.createSupplier(Supplier.builder()
                .name("Lácteos La Vaca Feliz")
                .contactName("Pedro Gómez")
                .email("ventas@lavacafeliz.com")
                .phone("+57 312 000 0003")
                .address("Cll 50 # 20-10, Medellín")
                .build());

        log.info("[DataSeeder] ✔ 3 proveedores creados.");
    }

    private void seedProducts() {
        if (!productRepository.findAll().isEmpty()) {
            log.info("[DataSeeder] Productos ya existen — omitiendo seed.");
            return;
        }
        log.info("[DataSeeder] Creando productos de ejemplo...");

        productUseCase.createProduct(Product.builder()
                .name("Lomo de res")
                .category("CARNES")
                .quantity(15.0).minimumQuantity(5.0)
                .unit("kg").price(new BigDecimal("32000"))
                .expirationDate(LocalDate.now().plusDays(5))
                .description("Lomo fino de res para medallones")
                .build());

        productUseCase.createProduct(Product.builder()
                .name("Pechuga de pollo")
                .category("CARNES")
                .quantity(20.0).minimumQuantity(8.0)
                .unit("kg").price(new BigDecimal("12000"))
                .expirationDate(LocalDate.now().plusDays(4))
                .description("Pechuga entera sin hueso")
                .build());

        productUseCase.createProduct(Product.builder()
                .name("Cebolla cabezona")
                .category("VERDURAS")
                .quantity(10.0).minimumQuantity(3.0)
                .unit("kg").price(new BigDecimal("2500"))
                .expirationDate(LocalDate.now().plusDays(14))
                .description("Cebolla blanca cabezona")
                .build());

        productUseCase.createProduct(Product.builder()
                .name("Tomate chonto")
                .category("VERDURAS")
                .quantity(8.0).minimumQuantity(3.0)
                .unit("kg").price(new BigDecimal("2000"))
                .expirationDate(LocalDate.now().plusDays(7))
                .description("Tomate maduro para salsas")
                .build());

        productUseCase.createProduct(Product.builder()
                .name("Papa pastusa")
                .category("VERDURAS")
                .quantity(25.0).minimumQuantity(10.0)
                .unit("kg").price(new BigDecimal("1800"))
                .expirationDate(LocalDate.now().plusDays(30))
                .description("Papa pastusa lavada")
                .build());

        productUseCase.createProduct(Product.builder()
                .name("Crema de leche")
                .category("LACTEOS")
                .quantity(5.0).minimumQuantity(2.0)
                .unit("l").price(new BigDecimal("4500"))
                .expirationDate(LocalDate.now().plusDays(10))
                .description("Crema de leche entera")
                .build());

        productUseCase.createProduct(Product.builder()
                .name("Mantequilla")
                .category("LACTEOS")
                .quantity(3.0).minimumQuantity(1.0)
                .unit("kg").price(new BigDecimal("15000"))
                .expirationDate(LocalDate.now().plusDays(20))
                .description("Mantequilla sin sal")
                .build());

        productUseCase.createProduct(Product.builder()
                .name("Aceite vegetal")
                .category("OTROS")
                .quantity(6.0).minimumQuantity(2.0)
                .unit("l").price(new BigDecimal("8000"))
                .expirationDate(LocalDate.now().plusDays(180))
                .description("Aceite vegetal para cocinar")
                .build());

        productUseCase.createProduct(Product.builder()
                .name("Sal refinada")
                .category("OTROS")
                .quantity(4.0).minimumQuantity(1.0)
                .unit("kg").price(new BigDecimal("1200"))
                .expirationDate(LocalDate.now().plusDays(365))
                .description("Sal refinada de mesa")
                .build());

        log.info("[DataSeeder] ✔ 9 productos creados.");
    }

    private void seedRecipes() {
        if (!recipeRepository.findAll().isEmpty()) {
            log.info("[DataSeeder] Recetas ya existen — omitiendo seed.");
            return;
        }

        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            log.warn("[DataSeeder] No hay productos; no se pueden crear recetas.");
            return;
        }

        log.info("[DataSeeder] Creando recetas de ejemplo...");

        String lomoId    = findProductId(products, "Lomo de res");
        String pechuId   = findProductId(products, "Pechuga de pollo");
        String cebollaId = findProductId(products, "Cebolla cabezona");
        String tomateId  = findProductId(products, "Tomate chonto");
        String papaId    = findProductId(products, "Papa pastusa");
        String cremaId   = findProductId(products, "Crema de leche");
        String mantId    = findProductId(products, "Mantequilla");
        String aceiteId  = findProductId(products, "Aceite vegetal");

        recipeUseCase.createRecipe(Recipe.builder()
                .dishName("Medallón de res con papas")
                .category("PLATO_PRINCIPAL")
                .description("Medallón de lomo de res a la plancha con papas al vapor y salsa criolla")
                .price(new BigDecimal("45000"))
                .preparationTimeMinutes(25)
                .ingredientsList(List.of(
                        ingredient(lomoId,    "Lomo de res",    0.25, "kg"),
                        ingredient(papaId,    "Papa pastusa",   0.20, "kg"),
                        ingredient(cebollaId, "Cebolla cabezona", 0.05, "kg"),
                        ingredient(tomateId,  "Tomate chonto",  0.05, "kg"),
                        ingredient(aceiteId,  "Aceite vegetal", 0.02, "l")
                ))
                .build());

        recipeUseCase.createRecipe(Recipe.builder()
                .dishName("Pollo al ajillo con crema")
                .category("PLATO_PRINCIPAL")
                .description("Pechuga de pollo salteada con mantequilla, crema de leche y ajo")
                .price(new BigDecimal("32000"))
                .preparationTimeMinutes(20)
                .ingredientsList(List.of(
                        ingredient(pechuId,  "Pechuga de pollo", 0.20, "kg"),
                        ingredient(cremaId,  "Crema de leche",   0.10, "l"),
                        ingredient(mantId,   "Mantequilla",      0.03, "kg"),
                        ingredient(aceiteId, "Aceite vegetal",   0.02, "l")
                ))
                .build());

        recipeUseCase.createRecipe(Recipe.builder()
                .dishName("Salsa criolla")
                .category("ENTRADA")
                .description("Salsa fresca de tomate, cebolla y cilantro — acompañamiento clásico")
                .price(new BigDecimal("5000"))
                .preparationTimeMinutes(10)
                .ingredientsList(List.of(
                        ingredient(tomateId,  "Tomate chonto",    0.10, "kg"),
                        ingredient(cebollaId, "Cebolla cabezona", 0.05, "kg"),
                        ingredient(aceiteId,  "Aceite vegetal",   0.01, "l")
                ))
                .build());

        log.info("[DataSeeder] ✔ 3 recetas creadas.");
        log.info("[DataSeeder]   CREDENCIALES POR DEFECTO:");
        log.info("[DataSeeder]   admin@gestor.chef    / Admin123!");
        log.info("[DataSeeder]   cocina@gestor.chef   / Cocina123!");
        log.info("[DataSeeder]   contable@gestor.chef / Contable123!");
    }

    private String findProductId(List<Product> products, String name) {
        return products.stream()
                .filter(p -> p.getName().equals(name))
                .map(Product::getId)
                .findFirst()
                .orElse(null);
    }

    private RecipeIngredient ingredient(String productId, String productName,
                                        double amount, String unit) {
        return RecipeIngredient.builder()
                .productId(productId)
                .productName(productName)
                .amount(amount)
                .unit(unit)
                .build();
    }
}
