import { api, unwrap } from './api';

export const authService = {
  async login(credentials) {
    const response = await api.post('/auth/login', credentials);
    return unwrap(response);
  },
  async register(payload) {
    const response = await api.post('/auth/register', payload);
    return unwrap(response);
  },
  async validate() {
    const response = await api.get('/auth/validate');
    return unwrap(response);
  },
  async refresh() {
    const response = await api.post('/auth/refresh');
    return unwrap(response);
  }
};
