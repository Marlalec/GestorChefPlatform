import { api, getPageParams, unwrap } from './api';

export const recipeService = {
  async getAll(params = {}) {
    const response = await api.get('/recipes', { params: { ...getPageParams(0, 100, 'dishName'), ...params } });
    return unwrap(response);
  },
  async getActive() {
    const response = await api.get('/recipes/active');
    return unwrap(response);
  },
  async create(payload) {
    const response = await api.post('/recipes', payload);
    return unwrap(response);
  },
  async update(id, payload) {
    const response = await api.put(`/recipes/${id}`, payload);
    return unwrap(response);
  },
  async remove(id) {
    const response = await api.delete(`/recipes/${id}`);
    return unwrap(response);
  },
  async checkAvailability(id, portions = 1) {
    const response = await api.get(`/recipes/${id}/availability`, { params: { portions } });
    return unwrap(response);
  },
  async calculateCost(id) {
    const response = await api.get(`/recipes/${id}/cost`);
    return unwrap(response);
  },
  async discountInventory(id, portions, userId) {
    const response = await api.post(`/recipes/${id}/discount-inventory`, { portions: Number(portions), userId });
    return unwrap(response);
  }
};
