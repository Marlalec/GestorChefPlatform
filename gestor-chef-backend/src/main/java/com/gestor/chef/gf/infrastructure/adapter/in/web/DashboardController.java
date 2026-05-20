package com.gestor.chef.gf.infrastructure.adapter.in.web;

import com.gestor.chef.gf.domain.model.DashboardStats;
import com.gestor.chef.gf.domain.port.in.DashboardUseCase;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Panel de control con indicadores clave de gestion de cocina (RF-008)")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardUseCase dashboardUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','COCINA','CONTABLE')")
    @Operation(
        summary = "Obtener indicadores del panel de control",
        description = "Retorna: productos con stock critico, productos proximos a vencer, " +
                      "compras recientes (7 dias), consumo semanal, perdidas estimadas por desperdicios " +
                      "(30 dias), ordenes pendientes y en progreso, ingresos estimados del dia, " +
                      "alertas sin leer y movimientos recientes de inventario. " +
                      "Roles: ADMIN, COCINA, CONTABLE."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Indicadores retornados correctamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token JWT ausente o invalido"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Rol sin permiso para acceder al dashboard")
    })
    public ResponseEntity<ApiResponse<DashboardStats>> getDashboard() {
        DashboardStats stats = dashboardUseCase.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }
}
