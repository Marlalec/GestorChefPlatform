import { api, unwrap } from './api';

export const reportService = {
  async getAll() {
    const response = await api.get('/reports');
    return unwrap(response);
  },
  async generate(type, payload) {
    const response = await api.post(`/reports/generate/${type}`, payload);
    return unwrap(response);
  },
  async downloadPdf(id) {
    const response = await api.get(`/reports/${id}/export/pdf`, { responseType: 'blob' });
    return response.data;
  },
  async downloadExcel(id) {
    const response = await api.get(`/reports/${id}/export/excel`, { responseType: 'blob' });
    return response.data;
  },
  async remove(id) {
    const response = await api.delete(`/reports/${id}`);
    return unwrap(response);
  }
};
