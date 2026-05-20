import { useMemo, useState } from 'react';
import { buildRequiredErrors, isPositive } from '../../utils/validation';

const initialState = { tableNumber: '', recipeId: '', quantity: 1, notes: '' };

export function OrderForm({ recipes = [], userId, onSubmit, onCancel, loading }) {
  const [values, setValues] = useState(initialState);
  const [errors, setErrors] = useState({});
  const recipe = useMemo(() => recipes.find((item) => item.id === values.recipeId), [recipes, values.recipeId]);

  function updateField(name, value) {
    setValues((current) => ({ ...current, [name]: value }));
  }

  function validate() {
    const nextErrors = buildRequiredErrors(values, ['tableNumber', 'recipeId', 'quantity']);
    if (!isPositive(values.quantity)) nextErrors.quantity = 'La cantidad debe ser mayor a cero';
    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  }

  function handleSubmit(event) {
    event.preventDefault();
    if (!validate() || !recipe) return;
    onSubmit({
      tableNumber: values.tableNumber.trim(),
      userId: userId || 'system',
      channel: 'DIRECT',
      notes: values.notes?.trim() || null,
      items: [{
        recipeId: recipe.id,
        dishName: recipe.dishName,
        quantity: Number(values.quantity),
        unitPrice: Number(recipe.price || 0),
        notes: values.notes?.trim() || null
      }]
    });
  }

  return (
    <form className="form" onSubmit={handleSubmit}>
      <div className="form-grid form-grid--2">
        <label className="label">
          <span>Mesa</span>
          <input className="input" value={values.tableNumber} onChange={(event) => updateField('tableNumber', event.target.value)} />
          {errors.tableNumber && <small>{errors.tableNumber}</small>}
        </label>
        <label className="label">
          <span>Plato</span>
          <select className="input" value={values.recipeId} onChange={(event) => updateField('recipeId', event.target.value)}>
            <option value="">Selecciona una receta</option>
            {recipes.map((item) => <option key={item.id} value={item.id}>{item.dishName}</option>)}
          </select>
          {errors.recipeId && <small>{errors.recipeId}</small>}
        </label>
        <label className="label">
          <span>Cantidad</span>
          <input className="input" type="number" min="1" value={values.quantity} onChange={(event) => updateField('quantity', event.target.value)} />
          {errors.quantity && <small>{errors.quantity}</small>}
        </label>
        <label className="label">
          <span>Precio unitario</span>
          <input className="input" value={recipe?.price || 0} readOnly />
        </label>
      </div>
      <label className="label">
        <span>Notas</span>
        <textarea className="textarea" rows="3" value={values.notes} onChange={(event) => updateField('notes', event.target.value)} />
      </label>
      <div className="row-actions">
        <button className="btn btn-ghost" type="button" onClick={onCancel}>Cancelar</button>
        <button className="btn btn-primary" type="submit" disabled={loading}>{loading ? 'Creando...' : 'Crear orden'}</button>
      </div>
    </form>
  );
}
