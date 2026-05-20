import { EmptyState } from '../common/EmptyState';
import { LoadingState } from '../common/LoadingState';

export function DataTable({ columns, rows, loading, emptyTitle, emptyDescription, rowKey = 'id' }) {
  if (loading) return <LoadingState />;
  if (!rows?.length) return <EmptyState title={emptyTitle} description={emptyDescription} />;
  return (
    <div className="table-scroll">
      <table className="table">
        <thead>
          <tr>{columns.map((column) => <th key={column.key} className={column.align === 'right' ? 't-right' : ''}>{column.header}</th>)}</tr>
        </thead>
        <tbody>
          {rows.map((row, index) => (
            <tr key={row[rowKey] || `${rowKey}-${index}`}>
              {columns.map((column) => (
                <td key={column.key} className={column.align === 'right' ? 't-right' : ''}>
                  {column.render ? column.render(row, index) : row[column.key]}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
