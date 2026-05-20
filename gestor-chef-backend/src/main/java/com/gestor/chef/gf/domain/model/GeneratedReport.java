package com.gestor.chef.gf.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedReport {
    private String id;
    private String type;
    private String title;
    private Map<String, Object> data;
    private Instant generatedAt;
    private String generatedBy;
    private String periodStart;
    private String periodEnd;
    private String format;
}
