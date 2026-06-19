import { authService } from './authService';

const API_TRANSACTION_BASE = import.meta.env.VITE_API_TRANSACTION_BASE || 'http://localhost:8082';

const buildUrl = (path) => `${API_TRANSACTION_BASE}${path}`;

const handleResponse = async (response) => {
    if (response.status === 204) {
        return { success: true, message: "Operation completed successfully" };
    }
    if (!response.ok) {
        const error = await response.json().catch(() => ({ message: `HTTP error! status: ${response.status}` }));
        throw new Error(error.message || `HTTP error! status: ${response.status}`);
    }
    return response.json();
};

export const httpClient = {
    async get(path) {
        const response = await authService.fetchWithAuth(buildUrl(path));
        return handleResponse(response);
    },

    async post(path, body) {
        const response = await authService.fetchWithAuth(buildUrl(path), {
            method: 'POST',
            body: JSON.stringify(body)
        });
        return handleResponse(response);
    },

    async put(path, body) {
        const response = await authService.fetchWithAuth(buildUrl(path), {
            method: 'PUT',
            body: JSON.stringify(body)
        });
        return handleResponse(response);
    },

    async delete(path) {
        const response = await authService.fetchWithAuth(buildUrl(path), {
            method: 'DELETE'
        });
        if (response.status === 204) return true;
        return handleResponse(response);
    },

    async getBlob(path) {
        const response = await authService.fetchWithAuth(buildUrl(path));
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.blob();
    }
};