package com.gestor.chef.gf.application.service.support;

import java.util.Optional;

public final class EntityFinder {

    private EntityFinder() {
    }

    public static <T> T required(Optional<T> value, String entityName, String id) {
        return value.orElseThrow(() -> new IllegalArgumentException(entityName + " no encontrado: " + id));
    }
}
