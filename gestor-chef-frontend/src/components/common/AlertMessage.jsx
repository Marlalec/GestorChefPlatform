export function AlertMessage({ type = 'info', message, onClose }) {
  if (!message) return null;
  return (
    <div className={`alert alert--${type}`}>
      <span>{message}</span>
      {onClose && <button type="button" onClick={onClose}>Cerrar</button>}
    </div>
  );
}
