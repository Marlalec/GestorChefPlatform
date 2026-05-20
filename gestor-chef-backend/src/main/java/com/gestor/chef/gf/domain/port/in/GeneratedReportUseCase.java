package com.gestor.chef.gf.domain.port.in;

import com.gestor.chef.gf.domain.model.GeneratedReport;
import java.util.List;
import java.util.Optional;

public interface GeneratedReportUseCase {
    GeneratedReport generateInventoryReport(String userId, String periodStart, String periodEnd);
    GeneratedReport generateFinancialReport(String userId, String periodStart, String periodEnd);
    GeneratedReport generateWasteReport(String userId, String periodStart, String periodEnd);
    GeneratedReport generateDemandProjection(String userId);
    Optional<GeneratedReport> getReportById(String id);
    List<GeneratedReport> getAllReports();
    void deleteReport(String id);
    List<GeneratedReport> getReportsByType(String type);
    List<GeneratedReport> getReportsByUser(String userId);
}
