import { useEffect, useState } from 'react';
import { ROLES } from '../../utils/constants';
import { buildRequiredErrors, isEmail } from '../../utils/validation';

const initialState = { name: '', email: '', rol: 'COCINA', phone: '', accountStatus: 'ACTIVE' };

export function UserForm({ initialValue, onSubmit, onCancel, loading }) {
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
    const nextErrors = buildRequiredErrors(values, ['name', 'email', 'rol']);
    if (!isEmail(values.email)) nextErrors.email = 'Email inválido';
    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  }

  function handleSubmit(event) {
    event.preventDefault();
    if (!validate()) return;
    onSubmit({
      name: values.name.trim(),
      email: values.email.trim(),
      rol: values.rol,
      phone: values.phone?.trim() || null,
      accountStatus: values.accountStatus || 'ACTIVE'
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
          <span>Email</span>
          <input className="input" type="email" value={values.email} onChange={(event) => updateField('email', event.target.value)} />
          {errors.email && <small>{errors.email}</small>}
        </label>
        <label className="label">
          <span>Rol</span>
          <select className="input" value={values.rol} onChange={(event) => updateField('rol', event.target.value)}>
            {ROLES.map((role) => <option key={role} value={role}>{role}</option>)}
          </select>
        </label>
        <label className="label">
          <span>Teléfono</span>
          <input className="input" value={values.phone || ''} onChange={(event) => updateField('phone', event.target.value)} />
        </label>
      </div>
      <div className="row-actions">
        <button className="btn btn-ghost" type="button" onClick={onCancel}>Cancelar</button>
        <button className="btn btn-primary" type="submit" disabled={loading}>{loading ? 'Guardando...' : 'Guardar usuario'}</button>
      </div>
    </form>
  );
}
