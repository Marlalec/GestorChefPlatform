import { useCallback, useMemo, useState } from 'react';
import { AlertMessage } from '../../components/common/AlertMessage';
import { ConfirmButton } from '../../components/common/ConfirmButton';
import { KpiCard } from '../../components/common/KpiCard';
import { Modal } from '../../components/common/Modal';
import { PageHeader } from '../../components/common/PageHeader';
import { StatusBadge } from '../../components/common/StatusBadge';
import { ProductForm } from '../../components/forms/ProductForm';
import { DataTable } from '../../components/tables/DataTable';
import { useAsyncAction } from '../../hooks/useAsyncAction';
import { useResource } from '../../hooks/useResource';
import { productService } from '../../services/productService';
import { supplierService } from '../../services/supplierService';
import { formatCategory, formatCurrency, formatDate, formatNumber } from '../../utils/formatters';

export function ProductsPage() {
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [search, setSearch] = useState('');
  const fetchProducts = useCallback(() => productService.getAll(), []);
  const fetchSuppliers = useCallback(() => supplierService.getActive(), []);
  const { items: products, loading, error, reload } = useResource(fetchProducts);
  const { items: suppliers } = useResource(fetchSuppliers);
  const action = useAsyncAction();

  const filteredProducts = useMemo(() => {
    const term = search.trim().toLowerCase();
    if (!term) return products;
    return products.filter((product) => `${product.name} ${product.category} ${product.supplierName || ''}`.toLowerCase().includes(term));
  }, [products, search]);

  const lowStockCount = products.filter((product) => Number(product.quantity) < Number(product.minimumQuantity)).length;

  function openCreate() {
    setEditing(null);
    setModalOpen(true);
  }

  function openEdit(product) {
    setEditing(product);
    setModalOpen(true);
  }

  async function handleSubmit(payload) {
    await action.run(async () => {
      if (editing?.id) await productService.update(editing.id, payload);
      else await productService.create(payload);
      await reload();
      setModalOpen(false);
    }, editing ? 'Producto actualizado correctamente' : 'Producto creado correctamente');
  }

  async function handleDelete(product) {
    await action.run(async () => {
      await productService.remove(product.id);
      await reload();
    }, 'Producto eliminado correctamente');
  }

  return (
    <>
      <PageHeader
        title="Productos e insumos"
        subtitle="Gestiona el catálogo de insumos, productos, cantidades, unidades de medida y estados disponibles."
        actions={<button className="btn btn-primary" type="button" onClick={openCreate}>Nuevo producto</button>}
      />
      <AlertMessage type="error" message={error || action.error} />
      <AlertMessage type="success" message={action.success} onClose={action.clearMessages} />
      <section className="kpi-grid">
        <KpiCard icon="restaurant_menu" value={products.length} label="Productos registrados" variant="activos" />
        <KpiCard icon="warning" value={lowStockCount} label="Bajo stock" variant="alertas" />
        <KpiCard icon="payments" value={formatCurrency(products.reduce((total, product) => total + Number(product.price || 0) * Number(product.quantity || 0), 0))} label="Valor estimado" variant="desperdicio" />
        <KpiCard icon="category" value={new Set(products.map((product) => product.category)).size} label="Categorías usadas" variant="pendientes" />
      </section>
      <section className="card table-card">
        <div className="section-head">
          <h3>Listado de productos</h3>
          <input className="input search-input" value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Buscar por nombre, categoría o proveedor" />
        </div>
        <DataTable
          loading={loading}
          rows={filteredProducts}
          emptyTitle="Sin productos"
          emptyDescription="Crea un producto para iniciar el control de inventario."
          columns={[
            { key: 'name', header: 'Producto' },
            { key: 'category', header: 'Categoría', render: (row) => formatCategory(row.category) },
            { key: 'supplierName', header: 'Proveedor', render: (row) => row.supplierName || 'Sin proveedor' },
            { key: 'quantity', header: 'Stock', align: 'right', render: (row) => `${formatNumber(row.quantity, 2)} ${row.unit}` },
            { key: 'price', header: 'Precio', align: 'right', render: (row) => formatCurrency(row.price) },
            { key: 'expirationDate', header: 'Vence', render: (row) => formatDate(row.expirationDate) },
            { key: 'status', header: 'Estado', render: (row) => <StatusBadge value={row.status} /> },
            { key: 'actions', header: 'Acciones', align: 'right', render: (row) => <div className="table-actions"><button className="btn btn-ghost" type="button" onClick={() => openEdit(row)}>Editar</button><ConfirmButton onConfirm={() => handleDelete(row)} message="El producto pasará a estado inactivo. ¿Confirmas?">Eliminar</ConfirmButton></div> }
          ]}
        />
      </section>
      <Modal open={modalOpen} title={editing ? 'Editar producto' : 'Nuevo producto'} onClose={() => setModalOpen(false)} size="lg">
        <ProductForm initialValue={editing} suppliers={suppliers} onSubmit={handleSubmit} onCancel={() => setModalOpen(false)} loading={action.loading} />
      </Modal>
    </>
  );
}
