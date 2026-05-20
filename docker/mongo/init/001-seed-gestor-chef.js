const databaseName = process.env.MONGO_INITDB_DATABASE || 'gestor_chef_runtime_db';
const appDb = db.getSiblingDB(databaseName);

appDb.users.createIndex({ email: 1 }, { unique: true });
appDb.products.createIndex({ name: 1 });
appDb.suppliers.createIndex({ name: 1 });
appDb.inventory.createIndex({ productId: 1 });
appDb.inventory_movements.createIndex({ productId: 1 });
appDb.orders.createIndex({ status: 1 });
appDb.alerts_notifications.createIndex({ userId: 1, status: 1 });
appDb.generated_reports.createIndex({ type: 1 });

const now = new Date();

appDb.roles.updateOne(
  { name: 'ADMIN' },
  {
    $setOnInsert: {
      name: 'ADMIN',
      description: 'Administrador del sistema',
      permissions: ['ALL'],
      status: 'ACTIVE',
      createdAt: now,
      updatedAt: now
    }
  },
  { upsert: true }
);

appDb.roles.updateOne(
  { name: 'COCINA' },
  {
    $setOnInsert: {
      name: 'COCINA',
      description: 'Usuario operativo de cocina e inventario',
      permissions: ['PRODUCTS_READ', 'INVENTORY_MANAGE', 'ORDERS_MANAGE', 'RECIPES_MANAGE', 'WASTES_MANAGE'],
      status: 'ACTIVE',
      createdAt: now,
      updatedAt: now
    }
  },
  { upsert: true }
);

appDb.roles.updateOne(
  { name: 'CONTABLE' },
  {
    $setOnInsert: {
      name: 'CONTABLE',
      description: 'Usuario contable para reportes y proveedores',
      permissions: ['REPORTS_READ', 'SUPPLIERS_MANAGE', 'WASTES_READ'],
      status: 'ACTIVE',
      createdAt: now,
      updatedAt: now
    }
  },
  { upsert: true }
);

appDb.users.updateOne(
  { email: 'admin@gestor.chef' },
  {
    $setOnInsert: {
      name: 'Administrador Gestor Chef',
      email: 'admin@gestor.chef',
      // Contraseña: Admin123!
      password: '$2a$10$m3rlSGS2iJ/t985MLgpyPemZPS4Ik91XvDzbvJRz8x4zzbXV0M4aW',
      phone: '3000000000',
      rol: 'ADMIN',
      accountStatus: 'ACTIVE',
      createdAt: now,
      updatedAt: now
    }
  },
  { upsert: true }
);
