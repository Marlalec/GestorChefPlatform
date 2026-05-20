export function KpiCard({ icon, value, label, variant = 'activos', meta }) {
  return (
    <article className={`kpi kpi--${variant}`}>
      <div className="kpi__icon"><span className="material-symbols-rounded">{icon}</span></div>
      <div className="kpi__value">{value}</div>
      <div className="kpi__label">{label}</div>
      {meta && <div className="kpi__meta">{meta}</div>}
    </article>
  );
}
