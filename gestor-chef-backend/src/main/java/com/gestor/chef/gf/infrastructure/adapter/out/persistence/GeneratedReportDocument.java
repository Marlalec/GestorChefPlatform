package com.gestor.chef.gf.infrastructure.adapter.out.persistence;

import com.gestor.chef.gf.domain.model.GeneratedReport;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document(collection = "generated_reports")
public class GeneratedReportDocument {

    @Id private String id;
    @Indexed private String type;
    private String title;
    private Map<String, Object> data;
    @Indexed private Instant generatedAt;
    @Indexed private String generatedBy;
    private String periodStart;
    private String periodEnd;
    private String format;

    public static GeneratedReportDocument fromDomain(GeneratedReport r) {
        return GeneratedReportDocument.builder()
                .id(r.getId()).type(r.getType()).title(r.getTitle()).data(r.getData())
                .generatedAt(r.getGeneratedAt()).generatedBy(r.getGeneratedBy())
                .periodStart(r.getPeriodStart()).periodEnd(r.getPeriodEnd()).format(r.getFormat())
                .build();
    }

    public GeneratedReport toDomain() {
        return GeneratedReport.builder()
                .id(id).type(type).title(title).data(data).generatedAt(generatedAt)
                .generatedBy(generatedBy).periodStart(periodStart).periodEnd(periodEnd).format(format)
                .build();
    }
}
