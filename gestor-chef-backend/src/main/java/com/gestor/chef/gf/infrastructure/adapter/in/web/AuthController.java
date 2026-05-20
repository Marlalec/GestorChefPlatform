package com.gestor.chef.gf.infrastructure.adapter.in.web;

import com.gestor.chef.gf.domain.model.User;
import com.gestor.chef.gf.domain.port.in.AuthUseCase;
import com.gestor.chef.gf.domain.port.in.UserUseCase;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.ApiResponse;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.AuthResponse;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.LoginRequest;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.RegisterRequest;
import com.gestor.chef.gf.infrastructure.adapter.in.web.mapper.UserWebMapper;
import com.gestor.chef.gf.infrastructure.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Login, registro y validación JWT")
public class AuthController {

    private final AuthUseCase authUseCase;
    private final UserUseCase userUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Devuelve JWT Bearer token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        String token = authUseCase.login(request.getEmail(), request.getPassword());
        User user = userUseCase.getUserByEmail(request.getEmail()).orElseThrow();
        return ResponseEntity.ok(ApiResponse.ok("Login exitoso", UserWebMapper.toAuthResponse(token, user)));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Crea el usuario y devuelve token JWT listo para usar")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        User created = authUseCase.register(UserWebMapper.toUser(request));
        String token = jwtTokenProvider.generateTokenFromEmail(created.getEmail());
        return ResponseEntity.status(201).body(ApiResponse.ok("Usuario registrado", UserWebMapper.toAuthResponse(token, created)));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refrescar JWT token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestHeader("Authorization") String authHeader) {
        String token = extractBearerToken(authHeader);
        String newToken = authUseCase.refreshToken(token);
        return ResponseEntity.ok(ApiResponse.ok(AuthResponse.builder().token(newToken).type("Bearer").build()));
    }

    @GetMapping("/validate")
    @Operation(summary = "Validar token JWT")
    public ResponseEntity<ApiResponse<Boolean>> validate(@RequestHeader("Authorization") String authHeader) {
        String token = extractBearerToken(authHeader);
        return ResponseEntity.ok(ApiResponse.ok(authUseCase.validateToken(token)));
    }

    private String extractBearerToken(String authHeader) {
        return authHeader.replace("Bearer ", "");
    }
}
