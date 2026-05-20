export function Modal({ open, title, children, onClose, size = 'md' }) {
  if (!open) return null;
  return (
    <div className="modal" role="dialog" aria-modal="true">
      <button className="modal__backdrop" type="button" aria-label="Cerrar" onClick={onClose} />
      <div className={`modal__dialog modal__dialog--${size}`}>
        <header className="modal__header">
          <h3>{title}</h3>
          <button className="modal__close" type="button" onClick={onClose} aria-label="Cerrar">
            <span className="material-symbols-rounded">close</span>
          </button>
        </header>
        {children}
      </div>
    </div>
  );
}
