import { useCallback, useMemo, useState } from 'react';
import { AlertMessage } from '../../components/common/AlertMessage';
import { KpiCard } from '../../components/common/KpiCard';
import { Modal } from '../../components/common/Modal';
import { PageHeader } from '../../components/common/PageHeader';
import { WasteForm } from '../../components/forms/WasteForm';
import { DataTable } from '../../components/tables/DataTable';
import { useAuth } from '../../context/AuthContext';
import { useAsyncAction } from '../../hooks/useAsyncAction';
import { useResource } from '../../hooks/useResource';
import { productService } from '../../services/productService';
import { wasteService } from '../../services/wasteService';
import { formatCurrency, formatDateTime, formatNumber } from '../../utils/formatters';

export function WastesPage() {
  const { user } = useAuth();
  const [modalOpen, setModalOpen] = useState(false);
  const fetchWastes = useCallback(() => wasteService.getAll(), []);
  const fetchProducts = useCallback(() => productService.getAll(), []);
  const { items: wastes, loading, error, reload } = useResource(fetchWastes);
  const { items: products } = useResource(fetchProducts);
  const action = useAsyncAction();

  const totals = useMemo(() => ({
    count: wastes.length,
    quantity: wastes.reduce((total, item) => total + Number(item.quantityWasted || 0), 0),
    estimatedCost: wastes.reduce((total, item) => total + Number(item.estimatedCost || 0), 0),
    products: new Set(wastes.map((item) => item.productId)).size
  }), [wastes]);

  async function handleCreate(payload) {
    await action.run(async () => {
      await wasteService.create(payload);
      await reload();
      setModalOpen(false);
    }, 'Desperdicio registrado correctamente');
  }

  return (
    <>
      <PageHeader title="Desperdicios" subtitle="Gestiona las pérdidas o mermas del restaurante y mantén actualizado el inventario." actions={<button className="btn btn-primary" type="button" onClick={() => setModalOpen(true)}>Registrar desperdicio</button>} />
      <AlertMessage type="error" message={error || action.error} />
      <AlertMessage type="success" message={action.success} onClose={action.clearMessages} />
      <section className="kpi-grid">
        <KpiCard icon="delete_sweep" value={totals.count} label="Registros" variant="activos" />
        <KpiCard icon="scale" value={formatNumber(totals.quantity, 2)} label="Cantidad total" variant="pendientes" />
        <KpiCard icon="payments" value={formatCurrency(totals.estimatedCost)} label="Costo estimado" variant="desperdicio" />
        <KpiCard icon="restaurant_menu" value={totals.products} label="Productos afectados" variant="alertas" />
      </section>
      <section className="card table-card">
        <h3>Listado de desperdicios</h3>
        <DataTable
          loading={loading}
          rows={wastes}
          emptyTitle="Sin desperdicios"
          emptyDescription="No se han registrado mermas para el periodo actual."
          columns={[
            { key: 'productName', header: 'Producto' },
            { key: 'quantityWasted', header: 'Cantidad', align: 'right', render: (row) => `${formatNumber(row.quantityWasted, 2)} ${row.unit}` },
            { key: 'cause', header: 'Causa' },
            { key: 'estimatedCost', header: 'Costo estimado', align: 'right', render: (row) => formatCurrency(row.estimatedCost) },
            { key: 'reportedByName', header: 'Reportado por', render: (row) => row.reportedByName || row.reportedBy || 'Sistema' },
            { key: 'occurredAt', header: 'Fecha', render: (row) => formatDateTime(row.occurredAt) }
          ]}
        />
      </section>
      <Modal open={modalOpen} title="Registrar desperdicio" onClose={() => setModalOpen(false)} size="lg">
        <WasteForm products={products} user={user} onSubmit={handleCreate} onCancel={() => setModalOpen(false)} loading={action.loading} />
      </Modal>
    </>
  );
}
