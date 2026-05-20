import { api, getPageParams, unwrap } from './api';

export const productService = {
  async getAll(params = {}) {
    const response = await api.get('/products', { params: { ...getPageParams(0, 100, 'name'), ...params } });
    return unwrap(response);
  },
  async getById(id) {
    const response = await api.get(`/products/${id}`);
    return unwrap(response);
  },
  async create(payload) {
    const response = await api.post('/products', payload);
    return unwrap(response);
  },
  async update(id, payload) {
    const response = await api.put(`/products/${id}`, payload);
    return unwrap(response);
  },
  async remove(id) {
    const response = await api.delete(`/products/${id}`);
    return unwrap(response);
  },
  async getByCategory(category) {
    const response = await api.get(`/products/category/${category}`);
    return unwrap(response);
  },
  async getLowStock() {
    const response = await api.get('/products/low-stock');
    return unwrap(response);
  },
  async updateStock(id, quantity) {
    const response = await api.patch(`/products/${id}/stock`, { quantity: Number(quantity) });
    return unwrap(response);
  }
};
