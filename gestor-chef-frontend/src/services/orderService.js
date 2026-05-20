import { api, getPageParams, unwrap } from './api';

export const orderService = {
  async getAll(params = {}) {
    const response = await api.get('/orders', { params: { ...getPageParams(0, 100, 'createdAt'), ...params } });
    return unwrap(response);
  },
  async create(payload) {
    const response = await api.post('/orders', payload);
    return unwrap(response);
  },
  async update(id, payload) {
    const response = await api.put(`/orders/${id}`, payload);
    return unwrap(response);
  },
  async updateStatus(id, status, userId) {
    const response = await api.patch(`/orders/${id}/status`, { status, userId });
    return unwrap(response);
  },
  async cancel(id, userId = 'system') {
    const response = await api.delete(`/orders/${id}/cancel`, { params: { userId } });
    return unwrap(response);
  },
  async getByStatus(status) {
    const response = await api.get(`/orders/status/${status}`);
    return unwrap(response);
  }
};
