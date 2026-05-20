import { formatStatus } from '../../utils/formatters';

export function StatusBadge({ value }) {
  const normalized = String(value || '').toLowerCase().replaceAll('_', '-');
  return <span className={`status-badge status-badge--${normalized}`}>{formatStatus(value)}</span>;
}
