import { useCallback, useMemo, useState } from 'react';
import { AlertMessage } from '../../components/common/AlertMessage';
import { KpiCard } from '../../components/common/KpiCard';
import { Modal } from '../../components/common/Modal';
import { PageHeader } from '../../components/common/PageHeader';
import { StatusBadge } from '../../components/common/StatusBadge';
import { OrderForm } from '../../components/forms/OrderForm';
import { DataTable } from '../../components/tables/DataTable';
import { useAuth } from '../../context/AuthContext';
import { useAsyncAction } from '../../hooks/useAsyncAction';
import { useResource } from '../../hooks/useResource';
import { orderService } from '../../services/orderService';
import { recipeService } from '../../services/recipeService';
import { ORDER_STATUSES } from '../../utils/constants';
import { formatCurrency, formatDateTime } from '../../utils/formatters';
import { ORDER_CHANNEL_LABELS } from '../../utils/constants';

export function OrdersPage() {
  const { user } = useAuth();
  const [modalOpen, setModalOpen] = useState(false);
  const [filter, setFilter] = useState('ALL');
  const fetchOrders = useCallback(() => orderService.getAll(), []);
  const fetchRecipes = useCallback(() => recipeService.getActive(), []);
  const { items: orders, loading, error, reload } = useResource(fetchOrders);
  const { items: recipes } = useResource(fetchRecipes);
  const action = useAsyncAction();

  const filteredOrders = useMemo(() => filter === 'ALL' ? orders : orders.filter((order) => order.status === filter), [orders, filter]);

  async function handleCreate(payload) {
    await action.run(async () => {
      await orderService.create(payload);
      await reload();
      setModalOpen(false);
    }, 'Orden creada correctamente');
  }

  async function updateStatus(order, status) {
    await action.run(async () => {
      await orderService.updateStatus(order.id, status, user?.id || 'system');
      await reload();
    }, 'Estado de la orden actualizado');
  }

  async function cancelOrder(order) {
    await action.run(async () => {
      await orderService.cancel(order.id, user?.id || 'system');
      await reload();
    }, 'Orden cancelada');
  }

  return (
    <>
      <PageHeader title="Pedidos Mesa" subtitle="Creación y seguimiento de órdenes directas del restaurante." actions={<button className="btn btn-primary" type="button" onClick={() => setModalOpen(true)}>Nueva orden</button>} />
      <AlertMessage type="error" message={error || action.error} />
      <AlertMessage type="success" message={action.success} onClose={action.clearMessages} />
      <section className="kpi-grid">
        <KpiCard icon="receipt_long" value={orders.length} label="Órdenes" variant="activos" />
        <KpiCard icon="pending_actions" value={orders.filter((item) => item.status === 'PENDING').length} label="Pendientes" variant="pendientes" />
        <KpiCard icon="restaurant" value={orders.filter((item) => item.status === 'IN_PROGRESS').length} label="En proceso" variant="alertas" />
        <KpiCard icon="payments" value={formatCurrency(orders.reduce((total, order) => total + Number(order.totalAmount || 0), 0))} label="Total registrado" variant="desperdicio" />
      </section>
      <section className="card table-card">
        <div className="section-head">
          <h3>Listado de órdenes</h3>
          <select className="input search-input" value={filter} onChange={(event) => setFilter(event.target.value)}>
            <option value="ALL">Todas</option>
            {ORDER_STATUSES.map((status) => <option key={status} value={status}>{status}</option>)}
          </select>
        </div>
        <DataTable
          loading={loading}
          rows={filteredOrders}
          emptyTitle="Sin órdenes"
          emptyDescription="Crea una orden para iniciar el flujo de mesa."
          columns={[
            { key: 'tableNumber', header: 'Mesa' },
            { key: 'items', header: 'Ítems', align: 'right', render: (row) => row.items?.length || 0 },
            { key: 'totalAmount', header: 'Total', align: 'right', render: (row) => formatCurrency(row.totalAmount) },
            { key: 'status', header: 'Estado', render: (row) => <StatusBadge value={row.status} /> },
            { key: 'channel', header: 'Canal', render: (row) => ORDER_CHANNEL_LABELS[row.channel] || row.channel },
            { key: 'createdAt', header: 'Fecha', render: (row) => formatDateTime(row.createdAt) },
            { key: 'actions', header: 'Acciones', align: 'right', render: (row) => <div className="table-actions"><button className="btn btn-ghost" type="button" onClick={() => updateStatus(row, 'IN_PROGRESS')}>Procesar</button><button className="btn btn-ghost" type="button" onClick={() => updateStatus(row, 'COMPLETED')}>Completar</button><button className="btn btn-ghost" type="button" onClick={() => cancelOrder(row)}>Cancelar</button></div> }
          ]}
        />
      </section>
      <Modal open={modalOpen} title="Nueva orden" onClose={() => setModalOpen(false)} size="lg">
        <OrderForm recipes={recipes} userId={user?.id} onSubmit={handleCreate} onCancel={() => setModalOpen(false)} loading={action.loading} />
      </Modal>
    </>
  );
}
