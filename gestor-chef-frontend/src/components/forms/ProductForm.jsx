import { useEffect, useState } from 'react';
import { PRODUCT_CATEGORIES, UNITS, UNIT_LABELS } from '../../utils/constants';
import { buildRequiredErrors, isPositive, isPositiveOrZero } from '../../utils/validation';

const initialState = {
  name: '',
  category: 'OTROS',
  supplierId: '',
  supplierName: '',
  quantity: 0,
  minimumQuantity: 0,
  unit: 'unidades',
  price: '',
  expirationDate: '',
  description: ''
};

export function ProductForm({ initialValue, suppliers = [], onSubmit, onCancel, loading }) {
  const [values, setValues] = useState(initialState);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (initialValue) {
      setValues({
        ...initialState,
        ...initialValue,
        price: initialValue.price ?? '',
        expirationDate: initialValue.expirationDate || ''
      });
    } else {
      setValues(initialState);
    }
    setErrors({});
  }, [initialValue]);

  function updateField(name, value) {
    if (name === 'supplierId') {
      const supplier = suppliers.find((item) => item.id === value);
      setValues((current) => ({ ...current, supplierId: value, supplierName: supplier?.name || '' }));
      return;
    }
    setValues((current) => ({ ...current, [name]: value }));
  }

  function validate() {
    const nextErrors = buildRequiredErrors(values, ['name', 'category', 'unit', 'price']);
    if (!isPositive(values.price)) nextErrors.price = 'El precio debe ser mayor a cero';
    if (!isPositiveOrZero(values.quantity)) nextErrors.quantity = 'La cantidad no puede ser negativa';
    if (!isPositiveOrZero(values.minimumQuantity)) nextErrors.minimumQuantity = 'El mínimo no puede ser negativo';
    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  }

  function handleSubmit(event) {
    event.preventDefault();
    if (!validate()) return;
    onSubmit({
      name: values.name.trim(),
      category: values.category,
      supplierId: values.supplierId || null,
      supplierName: values.supplierName || null,
      quantity: Number(values.quantity),
      minimumQuantity: Number(values.minimumQuantity),
      unit: values.unit,
      price: Number(values.price),
      expirationDate: values.expirationDate || null,
      description: values.description?.trim() || null
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
          <span>Categoría</span>
          <select className="input" value={values.category} onChange={(event) => updateField('category', event.target.value)}>
            {PRODUCT_CATEGORIES.map((category) => <option key={category} value={category}>{category}</option>)}
          </select>
        </label>
        <label className="label">
          <span>Proveedor</span>
          <select className="input" value={values.supplierId || ''} onChange={(event) => updateField('supplierId', event.target.value)}>
            <option value="">Sin proveedor</option>
            {suppliers.map((supplier) => <option key={supplier.id} value={supplier.id}>{supplier.name}</option>)}
          </select>
        </label>
        <label className="label">
          <span>Unidad</span>
          <select className="input" value={values.unit} onChange={(event) => updateField('unit', event.target.value)}>
            {UNITS.map((unit) => <option key={unit} value={unit}>{UNIT_LABELS[unit]}</option>)}
          </select>
        </label>
        <label className="label">
          <span>Cantidad</span>
          <input className="input" type="number" min="0" step="0.01" value={values.quantity} onChange={(event) => updateField('quantity', event.target.value)} />
          {errors.quantity && <small>{errors.quantity}</small>}
        </label>
        <label className="label">
          <span>Cantidad mínima</span>
          <input className="input" type="number" min="0" step="0.01" value={values.minimumQuantity} onChange={(event) => updateField('minimumQuantity', event.target.value)} />
          {errors.minimumQuantity && <small>{errors.minimumQuantity}</small>}
        </label>
        <label className="label">
          <span>Precio</span>
          <input className="input" type="number" min="0" step="0.01" value={values.price} onChange={(event) => updateField('price', event.target.value)} />
          {errors.price && <small>{errors.price}</small>}
        </label>
        <label className="label">
          <span>Fecha de vencimiento</span>
          <input className="input" type="date" value={values.expirationDate || ''} onChange={(event) => updateField('expirationDate', event.target.value)} />
        </label>
      </div>
      <label className="label">
        <span>Descripción</span>
        <textarea className="textarea" rows="3" value={values.description || ''} onChange={(event) => updateField('description', event.target.value)} />
      </label>
      <div className="row-actions">
        <button className="btn btn-ghost" type="button" onClick={onCancel}>Cancelar</button>
        <button className="btn btn-primary" type="submit" disabled={loading}>{loading ? 'Guardando...' : 'Guardar producto'}</button>
      </div>
    </form>
  );
}
