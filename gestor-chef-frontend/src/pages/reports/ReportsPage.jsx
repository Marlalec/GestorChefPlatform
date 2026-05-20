import { useCallback, useState } from 'react';
import { AlertMessage } from '../../components/common/AlertMessage';
import { ConfirmButton } from '../../components/common/ConfirmButton';
import { KpiCard } from '../../components/common/KpiCard';
import { PageHeader } from '../../components/common/PageHeader';
import { DataTable } from '../../components/tables/DataTable';
import { useAuth } from '../../context/AuthContext';
import { useAsyncAction } from '../../hooks/useAsyncAction';
import { useResource } from '../../hooks/useResource';
import { reportService } from '../../services/reportService';
import { formatDateTime } from '../../utils/formatters';
import { REPORT_TYPES, REPORT_TYPE_LABELS } from '../../utils/constants';

export function ReportsPage() {
  const { user } = useAuth();
  const [form, setForm] = useState({ type: 'inventory', periodStart: '', periodEnd: '' });
  const fetchReports = useCallback(() => reportService.getAll(), []);
  const { items: reports, loading, error, reload } = useResource(fetchReports);
  const action = useAsyncAction();

  function updateField(name, value) {
    setForm((current) => ({ ...current, [name]: value }));
  }

  async function generateReport(event) {
    event.preventDefault();
    await action.run(async () => {
      await reportService.generate(form.type, {
        userId: user?.id || 'system',
        periodStart: form.periodStart,
        periodEnd: form.periodEnd
      });
      await reload();
    }, 'Reporte generado correctamente');
  }

  async function download(report, type) {
    await action.run(async () => {
      const blob = type === 'pdf' ? await reportService.downloadPdf(report.id) : await reportService.downloadExcel(report.id);
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `${report.title || 'reporte'}.${type === 'pdf' ? 'pdf' : 'xlsx'}`;
      link.click();
      URL.revokeObjectURL(url);
    });
  }

  async function remove(report) {
    await action.run(async () => {
      await reportService.remove(report.id);
      await reload();
    }, 'Reporte eliminado');
  }

  return (
    <>
      <PageHeader title="Reportes" subtitle="Generación y descarga de reportes de inventario, finanzas, mermas y demanda." />
      <AlertMessage type="error" message={error || action.error} />
      <AlertMessage type="success" message={action.success} onClose={action.clearMessages} />
      <section className="kpi-grid">
        <KpiCard icon="monitoring" value={reports.length} label="Reportes generados" variant="activos" />
        <KpiCard icon="inventory_2" value={reports.filter((item) => item.type === 'INVENTORY').length} label="Inventario" variant="pendientes" />
        <KpiCard icon="payments" value={reports.filter((item) => item.type === 'FINANCIAL').length} label="Financieros" variant="desperdicio" />
        <KpiCard icon="delete_sweep" value={reports.filter((item) => item.type === 'WASTE').length} label="Mermas" variant="alertas" />
      </section>
      <section className="card mb-16">
        <h3>Generar reporte</h3>
        <form className="form report-form" onSubmit={generateReport}>
          <label className="label">
            <span>Tipo</span>
            <select className="input" value={form.type} onChange={(event) => updateField('type', event.target.value)}>
              {REPORT_TYPES.map((type) => (
                <option key={type} value={type}>
                  {REPORT_TYPE_LABELS[type]}
                </option>
              ))}
            </select>
          </label>
          <label className="label">
            <span>Desde</span>
            <input className="input" type="date" value={form.periodStart} onChange={(event) => updateField('periodStart', event.target.value)} />
          </label>
          <label className="label">
            <span>Hasta</span>
            <input className="input" type="date" value={form.periodEnd} onChange={(event) => updateField('periodEnd', event.target.value)} />
          </label>
          <button className="btn btn-primary" type="submit" disabled={action.loading}>Generar</button>
        </form>
      </section>
      <section className="card table-card">
        <h3>Reportes generados</h3>
        <DataTable
          loading={loading}
          rows={reports}
          emptyTitle="Sin reportes"
          emptyDescription="Genera un reporte para verlo en esta tabla."
          columns={[
            { key: 'title', header: 'Título' },
            { key: 'type', header: 'Tipo', render: (row) => REPORT_TYPE_LABELS[String(row.type || '').toLowerCase()] || row.type},
            { key: 'periodStart', header: 'Desde', render: (row) => row.periodStart || 'N/A' },
            { key: 'periodEnd', header: 'Hasta', render: (row) => row.periodEnd || 'N/A' },
            { key: 'generatedAt', header: 'Generado', render: (row) => formatDateTime(row.generatedAt) },
            { key: 'actions', header: 'Acciones', align: 'right', render: (row) => <div className="table-actions"><button className="btn btn-ghost" type="button" onClick={() => download(row, 'pdf')}>PDF</button><button className="btn btn-ghost" type="button" onClick={() => download(row, 'excel')}>Excel</button><ConfirmButton onConfirm={() => remove(row)}>Eliminar</ConfirmButton></div> }
          ]}
        />
      </section>
    </>
  );
}
