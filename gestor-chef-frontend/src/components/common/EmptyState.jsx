export function EmptyState({ title = 'Sin información', description = 'No hay registros para mostrar.' }) {
  return (
    <div className="empty-state">
      <span className="material-symbols-rounded">inbox</span>
      <h3>{title}</h3>
      <p>{description}</p>
    </div>
  );
}
