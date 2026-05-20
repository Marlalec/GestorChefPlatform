import { useCallback, useMemo, useState } from 'react';
import { AlertMessage } from '../../components/common/AlertMessage';
import { ConfirmButton } from '../../components/common/ConfirmButton';
import { KpiCard } from '../../components/common/KpiCard';
import { Modal } from '../../components/common/Modal';
import { PageHeader } from '../../components/common/PageHeader';
import { StatusBadge } from '../../components/common/StatusBadge';
import { SupplierForm } from '../../components/forms/SupplierForm';
import { DataTable } from '../../components/tables/DataTable';
import { useAsyncAction } from '../../hooks/useAsyncAction';
import { useResource } from '../../hooks/useResource';
import { supplierService } from '../../services/supplierService';
import { formatDateTime } from '../../utils/formatters';

export function SuppliersPage() {
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [search, setSearch] = useState('');
  const fetchSuppliers = useCallback(() => supplierService.getAll(), []);
  const { items: suppliers, loading, error, reload } = useResource(fetchSuppliers);
  const action = useAsyncAction();

  const filteredSuppliers = useMemo(() => {
    const term = search.trim().toLowerCase();
    if (!term) return suppliers;
    return suppliers.filter((supplier) => `${supplier.name} ${supplier.contactName || ''} ${supplier.email || ''}`.toLowerCase().includes(term));
  }, [suppliers, search]);

  function openCreate() {
    setEditing(null);
    setModalOpen(true);
  }

  function openEdit(supplier) {
    setEditing(supplier);
    setModalOpen(true);
  }

  async function handleSubmit(payload) {
    await action.run(async () => {
      if (editing?.id) await supplierService.update(editing.id, payload);
      else await supplierService.create(payload);
      await reload();
      setModalOpen(false);
    }, editing ? 'Proveedor actualizado correctamente' : 'Proveedor creado correctamente');
  }

  async function handleStatus(supplier, status) {
    await action.run(async () => {
      await supplierService.changeStatus(supplier.id, status);
      await reload();
    }, 'Estado actualizado correctamente');
  }

  async function handleDelete(supplier) {
    await action.run(async () => {
      await supplierService.remove(supplier.id);
      await reload();
    }, 'Proveedor eliminado correctamente');
  }

  return (
    <>
      <PageHeader title="Proveedores" subtitle="Administra la información de los proveedores que abastecen los productos e insumos del restaurante." 
            actions={<button className="btn btn-primary" type="button" onClick={openCreate}>Nuevo proveedor</button>} />
      <AlertMessage type="error" message={error || action.error} />
      <AlertMessage type="success" message={action.success} onClose={action.clearMessages} />
      <section className="kpi-grid">
        <KpiCard icon="local_shipping" value={suppliers.length} label="Proveedores" variant="activos" />
        <KpiCard icon="task_alt" value={suppliers.filter((item) => item.status === 'ACTIVE').length} label="Activos" variant="pendientes" />
        <KpiCard icon="block" value={suppliers.filter((item) => item.status === 'INACTIVE').length} label="Inactivos" variant="alertas" />
        <KpiCard icon="inventory_2" value={suppliers.reduce((total, item) => total + (item.productIds?.length || 0), 0)} label="Productos asociados" variant="desperdicio" />
      </section>
      <section className="card table-card">
        <div className="section-head">
          <h3>Listado de proveedores</h3>
          <input className="input search-input" value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Buscar proveedor" />
        </div>
        <DataTable
          loading={loading}
          rows={filteredSuppliers}
          emptyTitle="Sin proveedores"
          emptyDescription="Registra proveedores para asociarlos al catálogo de productos."
          columns={[
            { key: 'name', header: 'Proveedor' },
            { key: 'contactName', header: 'Contacto', render: (row) => row.contactName || 'Sin contacto' },
            { key: 'email', header: 'Email', render: (row) => row.email || 'Sin email' },
            { key: 'phone', header: 'Teléfono', render: (row) => row.phone || 'Sin teléfono' },
            { key: 'status', header: 'Estado', render: (row) => <StatusBadge value={row.status} /> },
            { key: 'updatedAt', header: 'Actualizado', render: (row) => formatDateTime(row.updatedAt) },
            { key: 'actions', header: 'Acciones', align: 'right', render: (row) => <div className="table-actions"><button className="btn btn-ghost" type="button" onClick={() => openEdit(row)}>Editar</button><button className="btn btn-ghost" type="button" onClick={() => handleStatus(row, row.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE')}>{row.status === 'ACTIVE' ? 'Inactivar' : 'Activar'}</button><ConfirmButton onConfirm={() => handleDelete(row)}>Eliminar</ConfirmButton></div> }
          ]}
        />
      </section>
      <Modal open={modalOpen} title={editing ? 'Editar proveedor' : 'Nuevo proveedor'} onClose={() => setModalOpen(false)} size="lg">
        <SupplierForm initialValue={editing} onSubmit={handleSubmit} onCancel={() => setModalOpen(false)} loading={action.loading} />
      </Modal>
    </>
  );
}
