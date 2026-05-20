import { useState } from 'react';
import { getErrorMessage } from '../services/api';

export function useAsyncAction() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  async function run(action, successMessage) {
    setLoading(true);
    setError('');
    setSuccess('');
    try {
      const result = await action();
      if (successMessage) setSuccess(successMessage);
      return result;
    } catch (err) {
      const message = getErrorMessage(err);
      setError(message);
      throw err;
    } finally {
      setLoading(false);
    }
  }

  function clearMessages() {
    setError('');
    setSuccess('');
  }

  return { loading, error, success, run, setError, setSuccess, clearMessages };
}
