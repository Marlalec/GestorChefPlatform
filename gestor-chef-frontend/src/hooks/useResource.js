import { useCallback, useEffect, useState } from 'react';
import { getErrorMessage } from '../services/api';
import { normalizePage } from '../utils/formatters';

export function useResource(fetcher, options = {}) {
  const [items, setItems] = useState([]);
  const [raw, setRaw] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const enabled = options.enabled !== false;

  const load = useCallback(async () => {
    if (!enabled) return;
    setLoading(true);
    setError('');
    try {
      const data = await fetcher();
      setRaw(data);
      setItems(normalizePage(data));
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, [enabled, fetcher]);

  useEffect(() => {
    load();
  }, [load]);

  return { items, raw, loading, error, reload: load, setItems, setRaw };
}
