import { KpiCard } from '../../components/common/KpiCard';
import { PageHeader } from '../../components/common/PageHeader';
import { DataTable } from '../../components/tables/DataTable';
import { ROLE_PERMISSIONS } from '../../utils/constants';
import { formatRole } from '../../utils/formatters';

const roles = [
  { id: 'ADMIN', description: 'Rol administrativo con acceso completo a la operación y administración de usuarios.', scope: 'Usuarios, productos, inventario, recetas, órdenes, proveedores, reportes, alertas y desperdicios' },
  { id: 'COCINA', description: 'Rol operativo para cocina e inventario.', scope: 'Productos, inventario, recetas, órdenes, alertas y desperdicios' },
  { id: 'CONTABLE', description: 'Rol orientado a gestión contable, proveedores, reportes y seguimiento de mermas.', scope: 'Proveedores, reportes, desperdicios, dashboard y alertas' }
];

export function RolesPage() {
  return (
    <>
      <PageHeader title="Roles" subtitle="Catálogo informativo alineado con la seguridad del backend. No se crean roles porque no existe controlador /roles implementado." />
      <section className="kpi-grid">
        <KpiCard icon="admin_panel_settings" value={roles.length} label="Roles definidos" variant="activos" />
        <KpiCard icon="lock" value="JWT" label="Seguridad aplicada" variant="pendientes" />
        <KpiCard icon="rule" value="RBAC" label="Control por rol" variant="alertas" />
        <KpiCard icon="api" value="/users" label="Gestión relacionada" variant="desperdicio" />
      </section>
      <section className="card table-card">
        <h3>Permisos por rol</h3>
        <DataTable
          rows={roles}
          rowKey="id"
          columns={[
            { key: 'id', header: 'Rol', render: (row) => formatRole(row.id) },
            { key: 'description', header: 'Descripción' },
            { key: 'scope', header: 'Alcance funcional' },
          ]}
        />
      </section>
    </>
  );
}
