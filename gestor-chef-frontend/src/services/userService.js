import { api, unwrap } from './api';

export const userService = {
  async getAll() {
    const response = await api.get('/users');
    return unwrap(response);
  },
  async getByRole(role) {
    const response = await api.get(`/users/role/${role}`);
    return unwrap(response);
  },
  async update(id, payload) {
    const response = await api.put(`/users/${id}`, payload);
    return unwrap(response);
  },
  async remove(id) {
    const response = await api.delete(`/users/${id}`);
    return unwrap(response);
  },
  async changeStatus(id, status) {
    const response = await api.patch(`/users/${id}/status`, { status });
    return unwrap(response);
  }
};
