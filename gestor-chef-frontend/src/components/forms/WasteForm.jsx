import { useMemo, useState } from 'react';
import { WASTE_CAUSES,WASTE_CAUSE_LABELS, UNIT_LABELS  } from '../../utils/constants';
import { buildRequiredErrors, isPositive } from '../../utils/validation';

const initialState = {
  productId: '',
  quantityWasted: '',
  cause: 'EXPIRY',
  description: '',
  occurredAt: ''
};

export function WasteForm({ products = [], user, onSubmit, onCancel, loading }) {
  const [values, setValues] = useState(initialState);
  const [errors, setErrors] = useState({});
  const product = useMemo(() => products.find((item) => item.id === values.productId), [products, values.productId]);
  const selectedProduct = products.find((product) => product.id === values.productId);
  const selectedUnit = selectedProduct?.unit || '';
  const selectedUnitLabel = selectedUnit ? UNIT_LABELS[selectedUnit] || selectedUnit : '';
  function updateField(name, value) {
    setValues((current) => ({ ...current, [name]: value }));
  }

  function validate() {
    const nextErrors = buildRequiredErrors(values, ['productId', 'quantityWasted', 'cause']);
    if (!isPositive(values.quantityWasted)) nextErrors.quantityWasted = 'La cantidad debe ser mayor a cero';
    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  }

  function handleSubmit(event) {
    event.preventDefault();
    if (!validate() || !product) return;
    onSubmit({
      productId: product.id,
      productName: product.name,
      quantityWasted: Number(values.quantityWasted),
      unit: product.unit,
      cause: values.cause,
      description: values.description?.trim() || null,
      reportedBy: user?.id || 'system',
      reportedByName: user?.name || 'Sistema',
      occurredAt: values.occurredAt ? new Date(values.occurredAt).toISOString() : new Date().toISOString()
    });
  }

  return (
    <form className="form" onSubmit={handleSubmit}>
      <div className="form-grid form-grid--2">
        <label className="label">
          <span>Producto</span>
          <select className="input" value={values.productId} onChange={(event) => updateField('productId', event.target.value)}>
            <option value="">Selecciona un producto</option>
            {products.map((item) => <option key={item.id} value={item.id}>{item.name}</option>)}
          </select>
          {errors.productId && <small>{errors.productId}</small>}
        </label>
        <label className="label">
          <span>Cantidad desperdiciada {selectedUnitLabel ? `(${selectedUnitLabel})` : ''}</span>
          <input className="input" type="number" min="0" step="0.01" value={values.quantityWasted}  placeholder={selectedUnitLabel ? `Ejemplo: 2 ${selectedUnitLabel.toLowerCase()}` : 'Selecciona un producto primero'}onChange={(event) => updateField('quantityWasted', event.target.value)} />
          {errors.quantityWasted && <small>{errors.quantityWasted}</small>}
        </label>
        <label className="label">
          <span>Causa</span>
          <select className="input" value={values.cause} onChange={(event) => updateField('cause', event.target.value)}>
            {WASTE_CAUSES.map((cause) => <option key={cause} value={cause}>{WASTE_CAUSE_LABELS[cause]}</option>)}
          </select>
        </label>
        <label className="label">
          <span>Fecha</span>
          <input className="input" type="datetime-local" value={values.occurredAt} onChange={(event) => updateField('occurredAt', event.target.value)} />
        </label>
      </div>
      <label className="label">
        <span>Descripción</span>
        <textarea className="textarea" rows="3" value={values.description} onChange={(event) => updateField('description', event.target.value)} />
      </label>
      <div className="row-actions">
        <button className="btn btn-ghost" type="button" onClick={onCancel}>Cancelar</button>
        <button className="btn btn-primary" type="submit" disabled={loading}>{loading ? 'Registrando...' : 'Registrar desperdicio'}</button>
      </div>
    </form>
  );
}
