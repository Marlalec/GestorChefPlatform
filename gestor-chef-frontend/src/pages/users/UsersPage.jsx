import { useCallback, useMemo, useState } from 'react';
import { AlertMessage } from '../../components/common/AlertMessage';
import { ConfirmButton } from '../../components/common/ConfirmButton';
import { KpiCard } from '../../components/common/KpiCard';
import { Modal } from '../../components/common/Modal';
import { PageHeader } from '../../components/common/PageHeader';
import { StatusBadge } from '../../components/common/StatusBadge';
import { UserForm } from '../../components/forms/UserForm';
import { DataTable } from '../../components/tables/DataTable';
import { useAsyncAction } from '../../hooks/useAsyncAction';
import { useResource } from '../../hooks/useResource';
import { userService } from '../../services/userService';
import { formatDateTime, formatRole } from '../../utils/formatters';

export function UsersPage() {
  const [editing, setEditing] = useState(null);
  const [search, setSearch] = useState('');
  const fetchUsers = useCallback(() => userService.getAll(), []);
  const { items: users, loading, error, reload } = useResource(fetchUsers);
  const action = useAsyncAction();

  const filteredUsers = useMemo(() => {
    const term = search.trim().toLowerCase();
    if (!term) return users;
    return users.filter((user) => `${user.name} ${user.email} ${user.rol}`.toLowerCase().includes(term));
  }, [users, search]);

  async function handleSubmit(payload) {
    await action.run(async () => {
      await userService.update(editing.id, payload);
      await reload();
      setEditing(null);
    }, 'Usuario actualizado correctamente');
  }

  async function handleStatus(user, status) {
    await action.run(async () => {
      await userService.changeStatus(user.id, status);
      await reload();
    }, 'Estado actualizado correctamente');
  }

  async function handleDelete(user) {
    await action.run(async () => {
      await userService.remove(user.id);
      await reload();
    }, 'Usuario eliminado correctamente');
  }

  return (
    <>
      <PageHeader title="Usuarios" subtitle="Administra los usuarios del sistema y consulta la información asociada a cada perfil." />
      <AlertMessage type="error" message={error || action.error} />
      <AlertMessage type="success" message={action.success} onClose={action.clearMessages} />
      <section className="kpi-grid">
        <KpiCard icon="groups" value={users.length} label="Usuarios" variant="activos" />
        <KpiCard icon="admin_panel_settings" value={users.filter((item) => item.rol === 'ADMIN').length} label="Administradores" variant="pendientes" />
        <KpiCard icon="restaurant" value={users.filter((item) => item.rol === 'COCINA').length} label="Cocina" variant="alertas" />
        <KpiCard icon="calculate" value={users.filter((item) => item.rol === 'CONTABLE').length} label="Contables" variant="desperdicio" />
      </section>
      <section className="card table-card">
        <div className="section-head">
          <h3>Listado de usuarios</h3>
          <input className="input search-input" value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Buscar usuario" />
        </div>
        <DataTable
          loading={loading}
          rows={filteredUsers}
          emptyTitle="Sin usuarios"
          emptyDescription="No hay usuarios registrados o tu rol no tiene acceso."
          columns={[
            { key: 'name', header: 'Nombre' },
            { key: 'email', header: 'Email' },
            { key: 'rol', header: 'Rol', render: (row) => formatRole(row.rol) },
            { key: 'accountStatus', header: 'Estado', render: (row) => <StatusBadge value={row.accountStatus} /> },
            { key: 'updatedAt', header: 'Actualizado', render: (row) => formatDateTime(row.updatedAt) },
            { key: 'actions', header: 'Acciones', align: 'right', render: (row) => <div className="table-actions"><button className="btn btn-ghost" type="button" onClick={() => setEditing(row)}>Editar</button><button className="btn btn-ghost" type="button" onClick={() => handleStatus(row, row.accountStatus === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE')}>{row.accountStatus === 'ACTIVE' ? 'Inactivar' : 'Activar'}</button><ConfirmButton onConfirm={() => handleDelete(row)}>Eliminar</ConfirmButton></div> }
          ]}
        />
      </section>
      <Modal open={Boolean(editing)} title="Editar usuario" onClose={() => setEditing(null)} size="lg">
        <UserForm initialValue={editing} onSubmit={handleSubmit} onCancel={() => setEditing(null)} loading={action.loading} />
      </Modal>
    </>
  );
}
