import { api, getPageParams, unwrap } from './api';

export const wasteService = {
  async getAll(params = {}) {
    const response = await api.get('/wastes', { params: { ...getPageParams(0, 100, 'occurredAt'), ...params } });
    return unwrap(response);
  },
  async create(payload) {
    const response = await api.post('/wastes', payload);
    return unwrap(response);
  },
  async getByProduct(productId) {
    const response = await api.get(`/wastes/product/${productId}`);
    return unwrap(response);
  }
};
