export const PRODUCT_CATEGORIES = ['CARNES', 'VERDURAS', 'LACTEOS', 'BEBIDAS', 'OTROS'];
export const RECIPE_CATEGORIES = ['ENTRADA', 'PLATO_PRINCIPAL', 'POSTRE', 'BEBIDA'];
export const UNITS = ['kg', 'g', 'l', 'ml', 'unidades'];
export const MOVEMENT_TYPES = ['IN', 'OUT', 'WASTE'];
export const MOVEMENT_REASONS = ['PURCHASE', 'RECIPE_USE', 'WASTE', 'SCALE_MEASUREMENT', 'ADJUSTMENT'];
export const ORDER_STATUSES = ['PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'];
export const STATUSES = ['ACTIVE', 'INACTIVE', 'PENDING', 'DELETE'];
export const SUPPLIER_STATUSES = ['ACTIVE', 'INACTIVE'];
export const ROLES = ['ADMIN', 'COCINA', 'CONTABLE'];
export const WASTE_CAUSES = ['EXPIRY', 'DETERIORATION', 'KITCHEN_ACCIDENT', 'OTHER'];
export const REPORT_TYPES = ['inventory', 'financial', 'waste', 'demand'];

export const REPORT_TYPE_LABELS = {
  inventory: 'Inventario',
  financial: 'Financiero',
  waste: 'Desperdicios',
  demand: 'Demanda'
};

export const ALERT_TYPE_LABELS = {
  STOCK_LOW: 'Stock bajo',
  WASTE_HIGH: 'Desperdicio alto',
  EXPIRATION_CLOSE: 'Próximo a vencer',
  SYSTEM: 'Sistema',
  EXPIRY: 'Vencimiento',
  OTHER: 'Otro'
};

export const WASTE_CAUSE_LABELS = {
  EXPIRY: 'Vencimiento',
  DETERIORATION: 'Deterioro',
  KITCHEN_ACCIDENT: 'Accidente en cocina',
  OTHER: 'Otro'
};

export const RECIPE_CATEGORY_LABELS = {
  ENTRADA: 'Entrada',
  PLATO_PRINCIPAL: 'Plato principal',
  POSTRE: 'Postre',
  BEBIDA: 'Bebida'
};

export const ORDER_CHANNEL_LABELS = {
  DIRECT: 'Mesa',
  WHATSAPP: 'WhatsApp'
};

export const UNIT_LABELS = {
  kg: 'Kilogramos',
  g: 'Gramos',
  l: 'Litros',
  ml: 'Mililitros',
  unidades: 'Unidades'
};

export const MOVEMENT_REASON_LABELS = {
  PURCHASE: 'Compra',
  RECIPE_USE: 'Uso en receta',
  WASTE: 'Desperdicio',
  SCALE_MEASUREMENT: 'Medición en báscula',
  ADJUSTMENT: 'Ajuste manual'
};

export const MOVEMENT_TYPE_LABELS = {
  IN: 'Entrada',
  OUT: 'Salida',
  WASTE: 'Desperdicio'
};

export const ROLE_LABELS = {
  ADMIN: 'Administrador',
  COCINA: 'Cocinero',
  CONTABLE: 'Contable'
};

export const STATUS_LABELS = {
  ACTIVE: 'Activo',
  INACTIVE: 'Inactivo',
  PENDING: 'Pendiente',
  DELETE: 'Delete',
  PENDING_ORDER: 'Pendiente',
  PENDING_STATUS: 'Pendiente',
  IN_PROGRESS: 'En proceso',
  COMPLETED: 'Completado',
  CANCELLED: 'Cancelado',
  READ: 'Leída',
  UNREAD: 'No leída'
};

export const CATEGORY_LABELS = {
  CARNES: 'Carnes',
  VERDURAS: 'Verduras',
  LACTEOS: 'Lácteos',
  BEBIDAS: 'Bebidas',
  OTROS: 'Otros',
  ENTRADA: 'Entrada',
  PLATO_PRINCIPAL: 'Plato principal',
  POSTRE: 'Postre',
  BEBIDA: 'Bebida'
};

export const ROLE_PERMISSIONS = {
  ADMIN: ['/dashboard', '/inventory', '/products', '/categories', '/recipes', '/orders', '/wastes', '/alerts', '/suppliers', '/reports', '/users', '/roles'],
  COCINA: ['/dashboard', '/inventory', '/products', '/categories', '/recipes', '/orders', '/wastes', '/alerts'],
  CONTABLE: ['/dashboard', '/suppliers', '/reports', '/wastes', '/alerts', '/roles']
};

export const PUBLIC_REGISTRATION_ROLES = ['COCINA', 'CONTABLE'];
