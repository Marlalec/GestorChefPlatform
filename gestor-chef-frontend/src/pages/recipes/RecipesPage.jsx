import { useCallback, useMemo, useState } from 'react';
import { AlertMessage } from '../../components/common/AlertMessage';
import { ConfirmButton } from '../../components/common/ConfirmButton';
import { KpiCard } from '../../components/common/KpiCard';
import { Modal } from '../../components/common/Modal';
import { PageHeader } from '../../components/common/PageHeader';
import { StatusBadge } from '../../components/common/StatusBadge';
import { RecipeForm } from '../../components/forms/RecipeForm';
import { DataTable } from '../../components/tables/DataTable';
import { useAsyncAction } from '../../hooks/useAsyncAction';
import { useResource } from '../../hooks/useResource';
import { productService } from '../../services/productService';
import { recipeService } from '../../services/recipeService';
import { formatCategory, formatCurrency } from '../../utils/formatters';

export function RecipesPage() {
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [search, setSearch] = useState('');
  const fetchRecipes = useCallback(() => recipeService.getAll(), []);
  const fetchProducts = useCallback(() => productService.getAll(), []);
  const { items: recipes, loading, error, reload } = useResource(fetchRecipes);
  const { items: products } = useResource(fetchProducts);
  const action = useAsyncAction();

  const filteredRecipes = useMemo(() => {
    const term = search.trim().toLowerCase();
    if (!term) return recipes;
    return recipes.filter((recipe) => `${recipe.dishName} ${recipe.category}`.toLowerCase().includes(term));
  }, [recipes, search]);

  function openCreate() {
    setEditing(null);
    setModalOpen(true);
  }

  function openEdit(recipe) {
    setEditing(recipe);
    setModalOpen(true);
  }

  async function handleSubmit(payload) {
    await action.run(async () => {
      if (editing?.id) await recipeService.update(editing.id, payload);
      else await recipeService.create(payload);
      await reload();
      setModalOpen(false);
    }, editing ? 'Receta actualizada correctamente' : 'Receta creada correctamente');
  }

  async function handleDelete(recipe) {
    await action.run(async () => {
      await recipeService.remove(recipe.id);
      await reload();
    }, 'Receta eliminada correctamente');
  }

  async function handleCost(recipe) {
    await action.run(async () => {
      const cost = await recipeService.calculateCost(recipe.id);
      window.alert(`Costo calculado: ${formatCurrency(cost)}`);
    });
  }

  return (
    <>
      <PageHeader title="Recetas" subtitle="Gestión del recetario y relación con productos del inventario." actions={<button className="btn btn-primary" type="button" onClick={openCreate}>Nueva receta</button>} />
      <AlertMessage type="error" message={error || action.error} />
      <AlertMessage type="success" message={action.success} onClose={action.clearMessages} />
      <section className="kpi-grid">
        <KpiCard icon="menu_book" value={recipes.length} label="Recetas" variant="activos" />
        <KpiCard icon="task_alt" value={recipes.filter((item) => item.status === 'ACTIVE').length} label="Activas" variant="pendientes" />
        <KpiCard icon="category" value={new Set(recipes.map((item) => item.category)).size} label="Categorías" variant="alertas" />
        <KpiCard icon="timer" value={`${Math.round(recipes.reduce((total, item) => total + Number(item.preparationTimeMinutes || 0), 0) / (recipes.length || 1))} min`} label="Promedio preparación" variant="desperdicio" />
      </section>
      <section className="card table-card">
        <div className="section-head">
          <h3>Listado de recetas</h3>
          <input className="input search-input" value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Buscar receta" />
        </div>
        <DataTable
          loading={loading}
          rows={filteredRecipes}
          emptyTitle="Sin recetas"
          emptyDescription="Crea recetas para habilitar pedidos de mesa."
          columns={[
            { key: 'dishName', header: 'Plato' },
            { key: 'category', header: 'Categoría', render: (row) => formatCategory(row.category) },
            { key: 'ingredientsList', header: 'Ingredientes', align: 'right', render: (row) => row.ingredientsList?.length || 0 },
            { key: 'price', header: 'Precio', align: 'right', render: (row) => formatCurrency(row.price) },
            { key: 'preparationTimeMinutes', header: 'Tiempo', align: 'right', render: (row) => `${row.preparationTimeMinutes} min` },
            { key: 'status', header: 'Estado', render: (row) => <StatusBadge value={row.status} /> },
            { key: 'actions', header: 'Acciones', align: 'right', render: (row) => <div className="table-actions"><button className="btn btn-ghost" type="button" onClick={() => openEdit(row)}>Editar</button><button className="btn btn-ghost" type="button" onClick={() => handleCost(row)}>Costo</button><ConfirmButton onConfirm={() => handleDelete(row)}>Eliminar</ConfirmButton></div> }
          ]}
        />
      </section>
      <Modal open={modalOpen} title={editing ? 'Editar receta' : 'Nueva receta'} onClose={() => setModalOpen(false)} size="xl">
        <RecipeForm initialValue={editing} products={products} onSubmit={handleSubmit} onCancel={() => setModalOpen(false)} loading={action.loading} />
      </Modal>
    </>
  );
}
