import { api, unwrap } from './api';

export const alertService = {
  async getAll() {
    const response = await api.get('/alerts');
    return unwrap(response);
  },
  async getUnread(userId) {
    const response = await api.get(`/alerts/unread/${userId}`);
    return unwrap(response);
  },
  async markAsRead(id) {
    const response = await api.patch(`/alerts/${id}/read`);
    return unwrap(response);
  },
  async markAllRead(userId) {
    const response = await api.patch(`/alerts/read-all/${userId}`);
    return unwrap(response);
  },
  async remove(id) {
    const response = await api.delete(`/alerts/${id}`);
    return unwrap(response);
  },
  async generateStock() {
    const response = await api.post('/alerts/generate-stock');
    return unwrap(response);
  },
  async generateExpiry() {
    const response = await api.post('/alerts/generate-expiry');
    return unwrap(response);
  }
};
