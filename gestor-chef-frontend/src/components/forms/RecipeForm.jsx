import { useEffect, useMemo, useState } from 'react';
import { RECIPE_CATEGORIES, RECIPE_CATEGORY_LABELS } from '../../utils/constants';
import { buildRequiredErrors, isPositive } from '../../utils/validation';

const initialIngredient = { productId: '', productName: '', amount: '', unit: 'unidades' };
const initialState = {
  dishName: '',
  category: 'PLATO_PRINCIPAL',
  description: '',
  price: '',
  preparationTimeMinutes: 20,
  ingredientsList: [{ ...initialIngredient }]
};

export function RecipeForm({ initialValue, products = [], onSubmit, onCancel, loading }) {
  const [values, setValues] = useState(initialState);
  const [errors, setErrors] = useState({});

  const productMap = useMemo(() => new Map(products.map((product) => [product.id, product])), [products]);

  useEffect(() => {
    setValues(initialValue ? {
      ...initialState,
      ...initialValue,
      price: initialValue.price ?? '',
      ingredientsList: initialValue.ingredientsList?.length ? initialValue.ingredientsList : [{ ...initialIngredient }]
    } : initialState);
    setErrors({});
  }, [initialValue]);

  function updateField(name, value) {
    setValues((current) => ({ ...current, [name]: value }));
  }

  function updateIngredient(index, name, value) {
    setValues((current) => {
      const ingredientsList = [...current.ingredientsList];
      const next = { ...ingredientsList[index], [name]: value };
      if (name === 'productId') {
        const product = productMap.get(value);
        next.productName = product?.name || '';
        next.unit = product?.unit || next.unit;
      }
      ingredientsList[index] = next;
      return { ...current, ingredientsList };
    });
  }

  function addIngredient() {
    setValues((current) => ({ ...current, ingredientsList: [...current.ingredientsList, { ...initialIngredient }] }));
  }

  function removeIngredient(index) {
    setValues((current) => ({ ...current, ingredientsList: current.ingredientsList.filter((_, currentIndex) => currentIndex !== index) }));
  }

  function validate() {
    const nextErrors = buildRequiredErrors(values, ['dishName', 'category', 'price', 'preparationTimeMinutes']);
    if (!isPositive(values.price)) nextErrors.price = 'El precio debe ser mayor a cero';
    if (!isPositive(values.preparationTimeMinutes)) nextErrors.preparationTimeMinutes = 'El tiempo debe ser mayor a cero';
    const validIngredients = values.ingredientsList.every((ingredient) => ingredient.productId && isPositive(ingredient.amount));
    if (!validIngredients) nextErrors.ingredientsList = 'Cada ingrediente requiere producto y cantidad mayor a cero';
    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  }

  function handleSubmit(event) {
    event.preventDefault();
    if (!validate()) return;
    onSubmit({
      dishName: values.dishName.trim(),
      category: values.category,
      description: values.description?.trim() || null,
      ingredientsList: values.ingredientsList.map((ingredient) => ({
        productId: ingredient.productId,
        productName: ingredient.productName,
        amount: Number(ingredient.amount),
        unit: ingredient.unit
      })),
      price: Number(values.price),
      preparationTimeMinutes: Number(values.preparationTimeMinutes)
    });
  }

  return (
    <form className="form" onSubmit={handleSubmit}>
      <div className="form-grid form-grid--2">
        <label className="label">
          <span>Plato</span>
          <input className="input" value={values.dishName} onChange={(event) => updateField('dishName', event.target.value)} />
          {errors.dishName && <small>{errors.dishName}</small>}
        </label>
        <label className="label">
          <span>Categoría</span>
          <select className="input" value={values.category} onChange={(event) => updateField('category', event.target.value)}>
            {RECIPE_CATEGORIES.map((category) => <option key={category} value={category}>{RECIPE_CATEGORY_LABELS[category]}</option>)}
          </select>
        </label>
        <label className="label">
          <span>Precio venta</span>
          <input className="input" type="number" min="0" step="0.01" value={values.price} onChange={(event) => updateField('price', event.target.value)} />
          {errors.price && <small>{errors.price}</small>}
        </label>
        <label className="label">
          <span>Tiempo preparación</span>
          <input className="input" type="number" min="1" value={values.preparationTimeMinutes} onChange={(event) => updateField('preparationTimeMinutes', event.target.value)} />
        </label>
      </div>
      <label className="label">
        <span>Descripción</span>
        <textarea className="textarea" rows="3" value={values.description || ''} onChange={(event) => updateField('description', event.target.value)} />
      </label>
      <section className="nested-card">
        <div className="section-head">
          <h4>Ingredientes</h4>
          <button className="btn btn-ghost" type="button" onClick={addIngredient}>Añadir ingrediente</button>
        </div>
        {errors.ingredientsList && <small className="form-error">{errors.ingredientsList}</small>}
        {values.ingredientsList.map((ingredient, index) => (
          <div className="ingredient-row" key={`${ingredient.productId}-${index}`}>
            <select className="input" value={ingredient.productId} onChange={(event) => updateIngredient(index, 'productId', event.target.value)}>
              <option value="">Producto</option>
              {products.map((product) => <option key={product.id} value={product.id}>{product.name}</option>)}
            </select>
            <input className="input" type="number" min="0" step="0.01" placeholder="Cantidad" value={ingredient.amount} onChange={(event) => updateIngredient(index, 'amount', event.target.value)} />
            <input className="input" placeholder="Unidad" value={ingredient.unit || ''} onChange={(event) => updateIngredient(index, 'unit', event.target.value)} />
            <button className="btn btn-ghost" type="button" onClick={() => removeIngredient(index)} disabled={values.ingredientsList.length === 1}>Quitar</button>
          </div>
        ))}
      </section>
      <div className="row-actions">
        <button className="btn btn-ghost" type="button" onClick={onCancel}>Cancelar</button>
        <button className="btn btn-primary" type="submit" disabled={loading}>{loading ? 'Guardando...' : 'Guardar receta'}</button>
      </div>
    </form>
  );
}
