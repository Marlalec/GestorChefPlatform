import { CATEGORY_LABELS, ROLE_LABELS, STATUS_LABELS } from './constants';

export function formatCurrency(value) {
  const number = Number(value || 0);
  return new Intl.NumberFormat('es-CO', {
    style: 'currency',
    currency: 'COP',
    maximumFractionDigits: 0
  }).format(number);
}

export function formatNumber(value, decimals = 0) {
  const number = Number(value || 0);
  return new Intl.NumberFormat('es-CO', {
    minimumFractionDigits: decimals,
    maximumFractionDigits: decimals
  }).format(number);
}

export function formatDate(value) {
  if (!value) return 'Sin fecha';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return new Intl.DateTimeFormat('es-CO', {
    year: 'numeric',
    month: 'short',
    day: '2-digit'
  }).format(date);
}

export function formatDateTime(value) {
  if (!value) return 'Sin fecha';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return new Intl.DateTimeFormat('es-CO', {
    year: 'numeric',
    month: 'short',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date);
}

export function formatRole(value) {
  return ROLE_LABELS[value] || value || 'Sin rol';
}

export function formatStatus(value) {
  return STATUS_LABELS[value] || value || 'Sin estado';
}

export function formatCategory(value) {
  return CATEGORY_LABELS[value] || value || 'Sin categoría';
}

export function normalizePage(data) {
  if (!data) return [];
  if (Array.isArray(data)) return data;
  if (Array.isArray(data.content)) return data.content;
  return [];
}

export function extractTotal(data) {
  if (!data) return 0;
  if (Array.isArray(data)) return data.length;
  return data.totalElements || data.content?.length || 0;
}
