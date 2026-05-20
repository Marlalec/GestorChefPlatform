package com.gestor.chef.gf.infrastructure.adapter.in.web;

import com.gestor.chef.gf.domain.model.User;
import com.gestor.chef.gf.domain.port.in.UserUseCase;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.ApiResponse;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.StatusUpdateRequest;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.UserResponse;
import com.gestor.chef.gf.infrastructure.adapter.in.web.mapper.UserWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Administración de usuarios y roles — acceso restringido a ADMIN")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserUseCase userUseCase;

    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAll() {
        List<UserResponse> users = userUseCase.getAllUsers().stream()
                .map(UserWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable String id) {
        return userUseCase.getUserById(id)
                .map(UserWebMapper::toResponse)
                .map(user -> ResponseEntity.ok(ApiResponse.ok(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar usuario por email")
    public ResponseEntity<ApiResponse<UserResponse>> getByEmail(@PathVariable String email) {
        return userUseCase.getUserByEmail(email)
                .map(UserWebMapper::toResponse)
                .map(user -> ResponseEntity.ok(ApiResponse.ok(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/role/{rol}")
    @Operation(summary = "Usuarios por rol: ADMIN | COCINA | CONTABLE")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getByRole(@PathVariable String rol) {
        List<UserResponse> users = userUseCase.getUsersByRole(rol).stream()
                .map(UserWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(users));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos del usuario")
    public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable String id, @RequestBody User user) {
        return ResponseEntity.ok(ApiResponse.ok(UserWebMapper.toResponse(userUseCase.updateUser(id, user))));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inactivar usuario")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        userUseCase.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.ok("Usuario eliminado", null));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Cambiar estado del usuario: ACTIVE | INACTIVE")
    public ResponseEntity<ApiResponse<UserResponse>> changeStatus(
            @PathVariable String id,
            @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(UserWebMapper.toResponse(userUseCase.changeAccountStatus(id, request.getStatus()))));
    }
}
