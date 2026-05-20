import { api, getPageParams, unwrap } from './api';

export const supplierService = {
  async getAll(params = {}) {
    const response = await api.get('/suppliers', { params: { ...getPageParams(0, 100, 'name'), ...params } });
    return unwrap(response);
  },
  async getActive() {
    const response = await api.get('/suppliers/active');
    return unwrap(response);
  },
  async create(payload) {
    const response = await api.post('/suppliers', payload);
    return unwrap(response);
  },
  async update(id, payload) {
    const response = await api.put(`/suppliers/${id}`, payload);
    return unwrap(response);
  },
  async remove(id) {
    const response = await api.delete(`/suppliers/${id}`);
    return unwrap(response);
  },
  async changeStatus(id, status) {
    const response = await api.patch(`/suppliers/${id}/status`, { status });
    return unwrap(response);
  }
};
