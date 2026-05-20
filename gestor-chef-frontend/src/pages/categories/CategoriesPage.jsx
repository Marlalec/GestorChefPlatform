import { useCallback, useMemo } from 'react';
import { AlertMessage } from '../../components/common/AlertMessage';
import { KpiCard } from '../../components/common/KpiCard';
import { PageHeader } from '../../components/common/PageHeader';
import { DataTable } from '../../components/tables/DataTable';
import { useResource } from '../../hooks/useResource';
import { productService } from '../../services/productService';
import { CATEGORY_LABELS, PRODUCT_CATEGORIES } from '../../utils/constants';
import { formatCurrency, formatNumber } from '../../utils/formatters';

export function CategoriesPage() {
  const fetchProducts = useCallback(() => productService.getAll(), []);
  const { items: products, loading, error } = useResource(fetchProducts);

  const categories = useMemo(() => PRODUCT_CATEGORIES.map((category) => {
    const items = products.filter((product) => product.category === category);
    return {
      id: category,
      name: CATEGORY_LABELS[category] || category,
      products: items.length,
      units: items.reduce((total, product) => total + Number(product.quantity || 0), 0),
      lowStock: items.filter((product) => Number(product.quantity || 0) < Number(product.minimumQuantity || 0)).length,
      estimatedValue: items.reduce((total, product) => total + Number(product.quantity || 0) * Number(product.price || 0), 0)
    };
  }), [products]);

  return (
    <>
      <PageHeader title="Categorías" subtitle="Por ahora, las categorías se encuentran predefinidas para mantener organizado el catálogo de productos e insumos." />
      <AlertMessage type="error" message={error} />
      <section className="kpi-grid">
        <KpiCard icon="category" value={categories.length} label="Categorías soportadas" variant="activos" />
        <KpiCard icon="restaurant_menu" value={products.length} label="Productos asociados" variant="pendientes" />
        <KpiCard icon="warning" value={categories.reduce((total, category) => total + category.lowStock, 0)} label="Alertas por categoría" variant="alertas" />
        <KpiCard icon="payments" value={formatCurrency(categories.reduce((total, category) => total + category.estimatedValue, 0))} label="Valor estimado" variant="desperdicio" />
      </section>
      <section className="card table-card">
        <h3>Resumen por categoría</h3>
        <DataTable
          loading={loading}
          rows={categories}
          rowKey="id"
          emptyTitle="Sin categorías"
          emptyDescription="No hay categorías configuradas en el frontend."
          columns={[
            { key: 'name', header: 'Categoría' },
            { key: 'products', header: 'Productos', align: 'right' },
            { key: 'units', header: 'Unidades', align: 'right', render: (row) => formatNumber(row.units, 2) },
            { key: 'lowStock', header: 'Bajo stock', align: 'right' },
            { key: 'estimatedValue', header: 'Valor estimado', align: 'right', render: (row) => formatCurrency(row.estimatedValue) }
          ]}
        />
      </section>
    </>
  );
}
