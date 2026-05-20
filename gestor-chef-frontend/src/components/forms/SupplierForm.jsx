import { useEffect, useState } from 'react';
import { buildRequiredErrors, isEmail } from '../../utils/validation';

const initialState = { name: '', contactName: '', email: '', phone: '', address: '' };

export function SupplierForm({ initialValue, onSubmit, onCancel, loading }) {
  const [values, setValues] = useState(initialState);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    setValues(initialValue ? { ...initialState, ...initialValue } : initialState);
    setErrors({});
  }, [initialValue]);

  function updateField(name, value) {
    setValues((current) => ({ ...current, [name]: value }));
  }

  function validate() {
    const nextErrors = buildRequiredErrors(values, ['name']);
    if (!isEmail(values.email)) nextErrors.email = 'Email inválido';
    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  }

  function handleSubmit(event) {
    event.preventDefault();
    if (!validate()) return;
    onSubmit({
      name: values.name.trim(),
      contactName: values.contactName?.trim() || null,
      email: values.email?.trim() || null,
      phone: values.phone?.trim() || null,
      address: values.address?.trim() || null
    });
  }

  return (
    <form className="form" onSubmit={handleSubmit}>
      <div className="form-grid form-grid--2">
        <label className="label">
          <span>Nombre</span>
          <input className="input" value={values.name} onChange={(event) => updateField('name', event.target.value)} />
          {errors.name && <small>{errors.name}</small>}
        </label>
        <label className="label">
          <span>Contacto</span>
          <input className="input" value={values.contactName || ''} onChange={(event) => updateField('contactName', event.target.value)} />
        </label>
        <label className="label">
          <span>Email</span>
          <input className="input" type="email" value={values.email || ''} onChange={(event) => updateField('email', event.target.value)} />
          {errors.email && <small>{errors.email}</small>}
        </label>
        <label className="label">
          <span>Teléfono</span>
          <input className="input" value={values.phone || ''} onChange={(event) => updateField('phone', event.target.value)} />
        </label>
      </div>
      <label className="label">
        <span>Dirección</span>
        <input className="input" value={values.address || ''} onChange={(event) => updateField('address', event.target.value)} />
      </label>
      <div className="row-actions">
        <button className="btn btn-ghost" type="button" onClick={onCancel}>Cancelar</button>
        <button className="btn btn-primary" type="submit" disabled={loading}>{loading ? 'Guardando...' : 'Guardar proveedor'}</button>
      </div>
    </form>
  );
}
