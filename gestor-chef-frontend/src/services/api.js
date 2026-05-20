import axios from 'axios';
import { clearSession, getStoredSession } from '../utils/auth';

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

api.interceptors.request.use((config) => {
  const session = getStoredSession();
  if (session?.token) {
    config.headers.Authorization = `Bearer ${session.token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      clearSession();
    }
    return Promise.reject(error);
  }
);

export function unwrap(response) {
  const payload = response.data;
  if (payload && Object.prototype.hasOwnProperty.call(payload, 'data')) return payload.data;
  return payload;
}

export function getErrorMessage(error) {
  const payload = error?.response?.data;
  if (typeof payload === 'string') return payload;
  if (payload?.message) return payload.message;
  if (payload?.error) return payload.error;
  if (error?.message) return error.message;
  return 'No fue posible procesar la solicitud';
}

export function getPageParams(page = 0, size = 100, sort) {
  const params = { page, size };
  if (sort) params.sort = sort;
  return params;
}
