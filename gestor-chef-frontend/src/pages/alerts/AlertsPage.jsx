import { useCallback, useState } from 'react';
import { AlertMessage } from '../../components/common/AlertMessage';
import { ConfirmButton } from '../../components/common/ConfirmButton';
import { KpiCard } from '../../components/common/KpiCard';
import { PageHeader } from '../../components/common/PageHeader';
import { StatusBadge } from '../../components/common/StatusBadge';
import { DataTable } from '../../components/tables/DataTable';
import { useAuth } from '../../context/AuthContext';
import { useAsyncAction } from '../../hooks/useAsyncAction';
import { useResource } from '../../hooks/useResource';
import { alertService } from '../../services/alertService';
import { formatDateTime } from '../../utils/formatters';
import { ALERT_TYPE_LABELS } from '../../utils/constants';

export function AlertsPage() {
  const { user } = useAuth();
  const [filter, setFilter] = useState('ALL');
  const fetchAlerts = useCallback(() => alertService.getAll(), []);
  const { items: alerts, loading, error, reload } = useResource(fetchAlerts);
  const action = useAsyncAction();

  const filteredAlerts = filter === 'ALL' ? alerts : alerts.filter((alert) => alert.status === filter);

  async function runGeneration(type) {
    await action.run(async () => {
      if (type === 'stock') await alertService.generateStock();
      else await alertService.generateExpiry();
      await reload();
    }, 'Alertas generadas correctamente');
  }

  async function markRead(alert) {
    await action.run(async () => {
      await alertService.markAsRead(alert.id);
      await reload();
    }, 'Alerta marcada como leída');
  }

  async function markAllRead() {
    await action.run(async () => {
      await alertService.markAllRead(user?.id || 'system');
      await reload();
    }, 'Alertas marcadas como leídas');
  }

  async function removeAlert(alert) {
    await action.run(async () => {
      await alertService.remove(alert.id);
      await reload();
    }, 'Alerta eliminada');
  }

  return (
    <>
      <PageHeader
        title="Alertas"
        subtitle="Consulta, generación y lectura de alertas del inventario."
        actions={<div className="page-actions"><button className="btn btn-ghost" type="button" onClick={() => runGeneration('stock')}>Generar stock</button><button className="btn btn-ghost" type="button" onClick={() => runGeneration('expiry')}>Generar vencimiento</button><button className="btn btn-primary" type="button" onClick={markAllRead}>Marcar todas</button></div>}
      />
      <AlertMessage type="error" message={error || action.error} />
      <AlertMessage type="success" message={action.success} onClose={action.clearMessages} />
      <section className="kpi-grid">
        <KpiCard icon="notifications" value={alerts.length} label="Alertas totales" variant="activos" />
        <KpiCard icon="mark_email_unread" value={alerts.filter((item) => item.status !== 'READ').length} label="Pendientes de lectura" variant="alertas" />
        <KpiCard icon="inventory_2" value={alerts.filter((item) => item.productId).length} label="Asociadas a productos" variant="pendientes" />
        <KpiCard icon="task_alt" value={alerts.filter((item) => item.status === 'READ').length} label="Leídas" variant="desperdicio" />
      </section>
      <section className="card table-card">
        <div className="section-head">
          <h3>Listado de alertas</h3>
          <select className="input search-input" value={filter} onChange={(event) => setFilter(event.target.value)}>
            <option value="ALL">Todas</option>
            <option value="UNREAD">No leídas</option>
            <option value="READ">Leídas</option>
          </select>
        </div>
        <DataTable
          loading={loading}
          rows={filteredAlerts}
          emptyTitle="Sin alertas"
          emptyDescription="No hay alertas generadas para mostrar."
          columns={[
            { key: 'title', header: 'Título' },
            { key: 'type', header: 'Tipo', render: (row) => ALERT_TYPE_LABELS[row.type] || row.type},
            { key: 'message', header: 'Mensaje' },
            { key: 'status', header: 'Estado', render: (row) => <StatusBadge value={row.status} /> },
            { key: 'createdAt', header: 'Creación', render: (row) => formatDateTime(row.createdAt) },
            { key: 'actions', header: 'Acciones', align: 'right', render: (row) => <div className="table-actions"><button className="btn btn-ghost" type="button" onClick={() => markRead(row)}>Leer</button><ConfirmButton onConfirm={() => removeAlert(row)}>Eliminar</ConfirmButton></div> }
          ]}
        />
      </section>
    </>
  );
}
