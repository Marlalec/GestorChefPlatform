import { useCallback, useMemo, useState } from 'react';
import { AlertMessage } from '../../components/common/AlertMessage';
import { KpiCard } from '../../components/common/KpiCard';
import { Modal } from '../../components/common/Modal';
import { PageHeader } from '../../components/common/PageHeader';
import { InventoryMovementForm } from '../../components/forms/InventoryMovementForm';
import { DataTable } from '../../components/tables/DataTable';
import { useAuth } from '../../context/AuthContext';
import { useAsyncAction } from '../../hooks/useAsyncAction';
import { useResource } from '../../hooks/useResource';
import { inventoryService } from '../../services/inventoryService';
import { productService } from '../../services/productService';
import { formatCategory, formatDateTime, formatNumber } from '../../utils/formatters';
import { MOVEMENT_TYPE_LABELS } from '../../utils/constants';

export function InventoryPage() {
  const { user } = useAuth();
  const [modalOpen, setModalOpen] = useState(false);
  const [search, setSearch] = useState('');
  const fetchInventory = useCallback(() => inventoryService.getAll(), []);
  const fetchMovements = useCallback(() => inventoryService.getMovements(), []);
  const fetchProducts = useCallback(() => productService.getAll(), []);
  const { items: inventory, loading, error, reload } = useResource(fetchInventory);
  const { items: movements, reload: reloadMovements } = useResource(fetchMovements);
  const { items: products } = useResource(fetchProducts);
  const action = useAsyncAction();

  const filteredInventory = useMemo(() => {
    const term = search.trim().toLowerCase();
    if (!term) return inventory;
    return inventory.filter((item) => `${item.productName} ${item.category}`.toLowerCase().includes(term));
  }, [inventory, search]);

  const totals = useMemo(() => ({
    skus: inventory.length,
    units: inventory.reduce((total, item) => total + Number(item.currentQuantity || 0), 0),
    low: inventory.filter((item) => item.lowStockAlert).length,
    movements: movements.length
  }), [inventory, movements]);

  async function handleSync() {
    await action.run(async () => {
      await inventoryService.sync();
      await reload();
    }, 'Inventario sincronizado correctamente');
  }

  async function handleMovement(payload) {
    await action.run(async () => {
      await inventoryService.createMovement(payload);
      await reload();
      await reloadMovements();
      setModalOpen(false);
    }, 'Movimiento registrado correctamente');
  }

  return (
    <>
      <PageHeader
        title="Inventario"
        subtitle="Gestiona y revisa la disponibilidad de insumos, niveles de stock y movimientos recientes del inventario."
        actions={<div className="page-actions"><button className="btn btn-ghost" type="button" onClick={handleSync} disabled={action.loading}>Sincronizar</button><button className="btn btn-primary" type="button" onClick={() => setModalOpen(true)}>Registrar movimiento</button></div>}
      />
      <AlertMessage type="error" message={error || action.error} />
      <AlertMessage type="success" message={action.success} onClose={action.clearMessages} />
      <section className="kpi-grid">
        <KpiCard icon="inventory_2" value={totals.skus} label="Productos registrados" variant="activos" />
        <KpiCard icon="format_list_numbered" value={formatNumber(totals.units, 2)} label="Unidades en stock" variant="pendientes" />
        <KpiCard icon="warning" value={totals.low} label="Bajo stock" variant="alertas" />
        <KpiCard icon="swap_vert" value={totals.movements} label="Movimientos" variant="desperdicio" />
      </section>
      <section className="two-col two-col--wide-left">
        <article className="card table-card">
          <div className="section-head">
            <h3>Estado de inventario</h3>
            <input className="input search-input" value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Buscar insumo" />
          </div>
          <DataTable
            loading={loading}
            rows={filteredInventory}
            emptyTitle="Inventario sin registros"
            emptyDescription="Sincroniza desde productos o crea movimientos para ver stock."
            columns={[
              { key: 'productName', header: 'Insumo' },
              { key: 'category', header: 'Categoría', render: (row) => formatCategory(row.category) },
              { key: 'currentQuantity', header: 'Cantidad', align: 'right', render: (row) => `${formatNumber(row.currentQuantity, 2)} ${row.unit}` },
              { key: 'minimumQuantity', header: 'Mínimo', align: 'right', render: (row) => `${formatNumber(row.minimumQuantity, 2)} ${row.unit}` },
              { key: 'lowStockAlert', header: 'Alerta', render: (row) => row.lowStockAlert ? <span className="status-badge status-badge--warning">Bajo stock</span> : <span className="status-badge status-badge--active">Normal</span> },
              { key: 'lastUpdated', header: 'Actualizado', render: (row) => formatDateTime(row.lastUpdated) }
            ]}
          />
        </article>
        <article className="card table-card">
          <h3>Últimos movimientos</h3>
          <DataTable
            loading={loading}
            rows={movements.slice(0, 10)}
            emptyTitle="Sin movimientos"
            emptyDescription="Los movimientos creados aparecerán en esta sección."
            columns={[
              { key: 'productName', header: 'Producto' },
              { key: 'movementType', header: 'Tipo',   render: (movement) => MOVEMENT_TYPE_LABELS[movement.movementType] || movement.movementType},
              { key: 'quantityChanged', header: 'Cantidad', align: 'right', render: (row) => formatNumber(row.quantityChanged, 2) },
              { key: 'timestamp', header: 'Fecha', render: (row) => formatDateTime(row.timestamp) }
            ]}
          />
        </article>
      </section>
      <Modal open={modalOpen} title="Registrar movimiento" onClose={() => setModalOpen(false)} size="lg">
        <InventoryMovementForm products={products} userId={user?.id} onSubmit={handleMovement} onCancel={() => setModalOpen(false)} loading={action.loading} />
      </Modal>
    </>
  );
}
