import { api, getPageParams, unwrap } from './api';

export const inventoryService = {
  async getAll() {
    const response = await api.get('/inventory');
    return unwrap(response);
  },
  async getKpis() {
    const response = await api.get('/inventory/kpis');
    return unwrap(response);
  },
  async getLowStock() {
    const response = await api.get('/inventory/low-stock');
    return unwrap(response);
  },
  async sync() {
    const response = await api.post('/inventory/sync');
    return unwrap(response);
  },
  async updateQuantity(productId, quantity) {
    const response = await api.patch(`/inventory/product/${productId}`, { quantity: Number(quantity) });
    return unwrap(response);
  },
  async getMovements(params = {}) {
    const response = await api.get('/inventory-movements', { params: { ...getPageParams(0, 100, 'timestamp'), ...params } });
    return unwrap(response);
  },
  async createMovement(payload) {
    const response = await api.post('/inventory-movements', payload);
    return unwrap(response);
  },
  async getMovementsByProduct(productId) {
    const response = await api.get(`/inventory-movements/product/${productId}`);
    return unwrap(response);
  }
};
