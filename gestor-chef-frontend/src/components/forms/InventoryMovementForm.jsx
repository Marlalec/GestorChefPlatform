import { useState } from 'react';
import { MOVEMENT_REASONS, MOVEMENT_TYPES, MOVEMENT_TYPE_LABELS, MOVEMENT_REASON_LABELS, UNIT_LABELS } from '../../utils/constants';
import { buildRequiredErrors, isPositive } from '../../utils/validation';

const initialState = {
  productId: '',
  quantityChanged: '',
  movementType: 'IN',
  reason: 'PURCHASE',
  notes: ''
};

export function InventoryMovementForm({ products = [], userId, onSubmit, onCancel, loading }) {
  const [values, setValues] = useState(initialState);
  const [errors, setErrors] = useState({});
  const selectedProduct = products.find((product) => product.id === values.productId);
  const selectedUnit = selectedProduct?.unit || '';
  const selectedUnitLabel = selectedUnit ? UNIT_LABELS[selectedUnit] || selectedUnit : '';

  function updateField(name, value) {
    setValues((current) => ({ ...current, [name]: value }));
  }

  function validate() {
    const nextErrors = buildRequiredErrors(values, ['productId', 'quantityChanged', 'movementType', 'reason']);
    if (!isPositive(values.quantityChanged)) nextErrors.quantityChanged = 'La cantidad debe ser mayor a cero';
    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  }

  function handleSubmit(event) {
    event.preventDefault();
    if (!validate()) return;
    onSubmit({
      productId: values.productId,
      quantityChanged: Number(values.quantityChanged),
      movementType: values.movementType,
      reason: values.reason,
      userId: userId || 'system',
      notes: values.notes?.trim() || null
    });
  }

  return (
    <form className="form" onSubmit={handleSubmit}>
      <div className="form-grid form-grid--2">
        <label className="label">
          <span>Producto</span>
          <select className="input" value={values.productId} onChange={(event) => updateField('productId', event.target.value)}>
            <option value="">Selecciona un producto</option>
            {products.map((product) => <option key={product.id} value={product.id}>{product.name}</option>)}
          </select>
          {errors.productId && <small>{errors.productId}</small>}
        </label>
        <label className="label">
          <span>Cantidad {selectedUnitLabel ? `(${selectedUnitLabel})` : ''}</span>
          <input className="input" type="number" min="0" step="0.01" value={values.quantityChanged} placeholder={selectedUnitLabel ? `Ejemplo: 10 ${selectedUnitLabel}` : 'Selecciona un producto primero'} onChange={(event) => updateField('quantityChanged', event.target.value)} />
          {errors.quantityChanged && <small>{errors.quantityChanged}</small>}
        </label>
        <label className="label">
          <span>Tipo de movimiento</span>
          <select className="input" value={values.movementType} onChange={(event) => updateField('movementType', event.target.value)}>
            {MOVEMENT_TYPES.map((type) => <option key={type} value={type}>{MOVEMENT_TYPE_LABELS[type]}</option>)}
          </select>
        </label>
        <label className="label">
          <span>Razón</span>
          <select className="input" value={values.reason} onChange={(event) => updateField('reason', event.target.value)}>
            {MOVEMENT_REASONS.map((reason) => <option key={reason} value={reason}>{MOVEMENT_REASON_LABELS[reason]}</option>)}
          </select>
        </label>
      </div>
      <label className="label">
        <span>Notas</span>
        <textarea className="textarea" rows="3" value={values.notes} onChange={(event) => updateField('notes', event.target.value)} />
      </label>
      <div className="row-actions">
        <button className="btn btn-ghost" type="button" onClick={onCancel}>Cancelar</button>
        <button className="btn btn-primary" type="submit" disabled={loading}>{loading ? 'Registrando...' : 'Registrar movimiento'}</button>
      </div>
    </form>
  );
}
