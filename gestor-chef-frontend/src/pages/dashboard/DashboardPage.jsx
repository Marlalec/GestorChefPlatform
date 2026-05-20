import { useCallback } from 'react';
import { KpiCard } from '../../components/common/KpiCard';
import { AlertMessage } from '../../components/common/AlertMessage';
import { PageHeader } from '../../components/common/PageHeader';
import { DataTable } from '../../components/tables/DataTable';
import { dashboardService } from '../../services/dashboardService';
import { useResource } from '../../hooks/useResource';
import { formatCurrency, formatDateTime, formatNumber } from '../../utils/formatters';
import { MOVEMENT_TYPE_LABELS } from '../../utils/constants';


export function DashboardPage() {
  const fetchDashboard = useCallback(() => dashboardService.getDashboard(), []);
  const { raw: stats, loading, error } = useResource(fetchDashboard);

  const movements = stats?.recentMovements || [];

  return (
    <>
      <PageHeader title="Resumen general" subtitle="Revisa de forma rápida la información más importante del restaurante y el estado actual del inventario." />
      <AlertMessage type="error" message={error} />
      <section className="kpi-grid">
        <KpiCard icon="inventory_2" value={loading ? '...' : stats?.totalActiveProducts || 0} label="Productos activos" variant="activos" />
        <KpiCard icon="warning" value={loading ? '...' : stats?.criticalProductsCount || 0} label="Productos críticos" variant="alertas" />
        <KpiCard icon="pending_actions" value={loading ? '...' : stats?.pendingOrdersCount || 0} label="Pedidos pendientes" variant="pendientes" />
        <KpiCard icon="payments" value={loading ? '...' : formatCurrency(stats?.todayRevenueEstimate)} label="Ventas estimadas hoy" variant="desperdicio" />
      </section>
      <section className="two-col">
        <article className="card table-card">
          <h3>Inventario con riesgo</h3>
          <DataTable
            loading={loading}
            rows={stats?.criticalProducts || []}
            emptyTitle="Sin productos críticos"
            emptyDescription="No hay productos por debajo del mínimo."
            columns={[
              { key: 'name', header: 'Producto' },
              { key: 'quantity', header: 'Cantidad', align: 'right', render: (row) => `${formatNumber(row.quantity, 2)} ${row.unit}` },
              { key: 'minimumQuantity', header: 'Mínimo', align: 'right', render: (row) => `${formatNumber(row.minimumQuantity, 2)} ${row.unit}` }
            ]}
          />
        </article>
        <article className="card table-card">
          <h3>Movimientos recientes</h3>
          <DataTable
            loading={loading}
            rows={movements}
            emptyTitle="Sin movimientos"
            emptyDescription="Todavía no hay movimientos recientes registrados."
            columns={[
              { key: 'productName', header: 'Producto' },
              { key: 'movementType', header: 'Tipo', render: (movement) => MOVEMENT_TYPE_LABELS[movement.movementType] || movement.movementType },
              { key: 'timestamp', header: 'Fecha', align: 'right', render: (row) => formatDateTime(row.timestamp) }
            ]}
          />
        </article>
      </section>
    </>
  );
}
