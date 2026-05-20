package com.gestor.chef.gf.infrastructure.adapter.in.web;

import com.gestor.chef.gf.application.service.ReportExportService;
import com.gestor.chef.gf.domain.model.GeneratedReport;
import com.gestor.chef.gf.domain.port.in.GeneratedReportUseCase;
import com.gestor.chef.gf.infrastructure.adapter.in.web.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Generación y consulta de reportes: financiero, inventario, mermas y demanda")
public class GeneratedReportController {

    private final GeneratedReportUseCase reportUseCase;
    private final ReportExportService    exportService;

    @GetMapping
    @Operation(summary = "Listar todos los reportes generados")
    public ResponseEntity<ApiResponse<List<GeneratedReport>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(reportUseCase.getAllReports()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GeneratedReport>> getById(@PathVariable String id) {
        return reportUseCase.getReportById(id)
                .map(r -> ResponseEntity.ok(ApiResponse.ok(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Reportes por tipo: FINANCIAL | INVENTORY | WASTE | DEMAND")
    public ResponseEntity<ApiResponse<List<GeneratedReport>>> getByType(@PathVariable String type) {
        return ResponseEntity.ok(ApiResponse.ok(reportUseCase.getReportsByType(type)));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Reportes generados por un usuario específico")
    public ResponseEntity<ApiResponse<List<GeneratedReport>>> getByUser(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.ok(reportUseCase.getReportsByUser(userId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        reportUseCase.deleteReport(id);
        return ResponseEntity.ok(ApiResponse.ok("Reporte eliminado", null));
    }

    @PostMapping("/generate/inventory")
    @Operation(summary = "Generar reporte de inventario actual",
               description = "Snapshot del stock: SKUs, unidades, valor, alertas de stock bajo.")
    public ResponseEntity<ApiResponse<GeneratedReport>> generateInventory(@RequestBody Map<String, String> body) {
        String userId      = body.getOrDefault("userId", "system");
        String periodStart = body.getOrDefault("periodStart", "");
        String periodEnd   = body.getOrDefault("periodEnd", "");
        return ResponseEntity.status(201).body(
                ApiResponse.ok("Reporte de inventario generado",
                        reportUseCase.generateInventoryReport(userId, periodStart, periodEnd)));
    }

    @PostMapping("/generate/financial")
    @Operation(summary = "Generar reporte financiero",
               description = "Ingresos por órdenes completadas, costo de ingredientes, margen bruto.")
    public ResponseEntity<ApiResponse<GeneratedReport>> generateFinancial(@RequestBody Map<String, String> body) {
        String userId      = body.getOrDefault("userId", "system");
        String periodStart = body.getOrDefault("periodStart", "");
        String periodEnd   = body.getOrDefault("periodEnd", "");
        return ResponseEntity.status(201).body(
                ApiResponse.ok("Reporte financiero generado",
                        reportUseCase.generateFinancialReport(userId, periodStart, periodEnd)));
    }

    @PostMapping("/generate/waste")
    @Operation(summary = "Generar reporte de mermas",
               description = "Movimientos tipo WASTE: unidades y valor perdido por producto.")
    public ResponseEntity<ApiResponse<GeneratedReport>> generateWaste(@RequestBody Map<String, String> body) {
        String userId      = body.getOrDefault("userId", "system");
        String periodStart = body.getOrDefault("periodStart", "");
        String periodEnd   = body.getOrDefault("periodEnd", "");
        return ResponseEntity.status(201).body(
                ApiResponse.ok("Reporte de mermas generado",
                        reportUseCase.generateWasteReport(userId, periodStart, periodEnd)));
    }

    @PostMapping("/generate/demand")
    @Operation(summary = "Generar proyección de demanda semanal",
               description = "Platos y productos más consumidos. Base para módulo IA.")
    public ResponseEntity<ApiResponse<GeneratedReport>> generateDemand(@RequestBody Map<String, String> body) {
        String userId = body.getOrDefault("userId", "system");
        return ResponseEntity.status(201).body(
                ApiResponse.ok("Proyección de demanda generada",
                        reportUseCase.generateDemandProjection(userId)));
    }

    @GetMapping(value = "/{id}/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Descargar reporte en PDF (RF-003)")
    public ResponseEntity<byte[]> exportPdf(@PathVariable String id) {
        GeneratedReport report = reportUseCase.getReportById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado: " + id));
        byte[] bytes = exportService.exportToPdf(report);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + sanitizeFilename(report.getTitle()) + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }

    @GetMapping(value = "/{id}/export/excel",
                produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @Operation(summary = "Descargar reporte en Excel / XLSX (RF-003)")
    public ResponseEntity<byte[]> exportExcel(@PathVariable String id) {
        GeneratedReport report = reportUseCase.getReportById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado: " + id));
        byte[] bytes = exportService.exportToExcel(report);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + sanitizeFilename(report.getTitle()) + ".xlsx\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }

    private String sanitizeFilename(String title) {
        if (title == null) return "reporte";
        return title.toLowerCase()
                    .replaceAll("[^a-z0-9áéíóúñ_\\- ]", "")
                    .replaceAll("\\s+", "_");
    }
}
