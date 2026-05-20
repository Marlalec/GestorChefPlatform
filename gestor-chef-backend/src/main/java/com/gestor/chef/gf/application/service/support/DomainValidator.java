package com.gestor.chef.gf.application.service.support;

import java.util.Set;

public final class DomainValidator {

    private DomainValidator() {
    }

    public static String requireAllowed(String value, Set<String> allowedValues, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " es obligatorio");
        }
        String normalized = value.trim().toUpperCase();
        if (!allowedValues.contains(normalized)) {
            throw new IllegalArgumentException(fieldName + " inválido: " + value);
        }
        return normalized;
    }

    public static double requirePositive(double value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " debe ser mayor a 0");
        }
        return value;
    }
}
