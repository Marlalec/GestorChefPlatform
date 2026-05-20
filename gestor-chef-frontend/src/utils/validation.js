export function required(value) {
  return value !== undefined && value !== null && String(value).trim() !== '';
}

export function isPositive(value) {
  return Number(value) > 0;
}

export function isPositiveOrZero(value) {
  return Number(value) >= 0;
}

export function isEmail(value) {
  if (!value) return true;
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
}

export function buildRequiredErrors(values, fields) {
  return fields.reduce((errors, field) => {
    if (!required(values[field])) errors[field] = 'Campo obligatorio';
    return errors;
  }, {});
}
