package com.gestor.chef.gf.domain.model.value;

import java.util.Set;

public final class DomainValues {

    private DomainValues() {
    }

    public static final class Status {
        public static final String ACTIVE = "ACTIVE";
        public static final String INACTIVE = "INACTIVE";
        public static final String PENDING = "PENDING";
        public static final String IN_PROGRESS = "IN_PROGRESS";
        public static final String COMPLETED = "COMPLETED";
        public static final String CANCELLED = "CANCELLED";
        public static final String UNREAD = "UNREAD";
        public static final String READ = "READ";
        public static final Set<String> ACCOUNT = Set.of(ACTIVE, INACTIVE);
        public static final Set<String> ORDER = Set.of(PENDING, IN_PROGRESS, COMPLETED, CANCELLED);
        public static final Set<String> ALERT = Set.of(UNREAD, READ);

        private Status() {
        }
    }

    public static final class Role {
        public static final String ADMIN = "ADMIN";
        public static final String COCINA = "COCINA";
        public static final String CONTABLE = "CONTABLE";
        public static final Set<String> ALL = Set.of(ADMIN, COCINA, CONTABLE);
        public static final Set<String> PUBLIC_REGISTRATION = Set.of(COCINA, CONTABLE);

        private Role() {
        }
    }

    public static final class MovementType {
        public static final String IN = "IN";
        public static final String OUT = "OUT";
        public static final String WASTE = "WASTE";
        public static final String ADJUSTMENT = "ADJUSTMENT";
        public static final Set<String> ALL = Set.of(IN, OUT, WASTE, ADJUSTMENT);

        private MovementType() {
        }
    }

    public static final class MovementReason {
        public static final String PURCHASE = "PURCHASE";
        public static final String RECIPE_USE = "RECIPE_USE";
        public static final String WASTE = "WASTE";
        public static final String SCALE_MEASUREMENT = "SCALE_MEASUREMENT";
        public static final String ADJUSTMENT = "ADJUSTMENT";
        public static final String WASTE_EXPIRY = "WASTE_EXPIRY";
        public static final String WASTE_DETERIORATION = "WASTE_DETERIORATION";
        public static final String WASTE_ACCIDENT = "WASTE_ACCIDENT";

        private MovementReason() {
        }
    }

    public static final class Channel {
        public static final String DIRECT = "DIRECT";
        public static final String WHATSAPP = "WHATSAPP";

        private Channel() {
        }
    }

    public static final class AlertType {
        public static final String STOCK_LOW = "STOCK_LOW";
        public static final String EXPIRY = "EXPIRY";
        public static final String SYSTEM = "SYSTEM";

        private AlertType() {
        }
    }

    public static final class ReportType {
        public static final String INVENTORY = "INVENTORY";
        public static final String FINANCIAL = "FINANCIAL";
        public static final String WASTE = "WASTE";
        public static final String DEMAND = "DEMAND";
        public static final String JSON = "JSON";

        private ReportType() {
        }
    }

    public static final class WasteCause {
        public static final String EXPIRY = "EXPIRY";
        public static final String DETERIORATION = "DETERIORATION";
        public static final String KITCHEN_ACCIDENT = "KITCHEN_ACCIDENT";

        private WasteCause() {
        }
    }
}
