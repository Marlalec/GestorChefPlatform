export function ConfirmButton({ children, message = '¿Confirmas esta acción?', onConfirm, className = 'btn btn-ghost', disabled }) {
  function handleClick() {
    if (window.confirm(message)) onConfirm?.();
  }
  return <button type="button" className={className} onClick={handleClick} disabled={disabled}>{children}</button>;
}
