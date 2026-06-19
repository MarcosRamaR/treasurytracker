import { httpClient } from './httpClient';

const TRANSACTIONS_PATH = '/api/transactions';
const buildQuery = (params) => {
    const query = Object.entries(params)
        .filter(([, v]) => v !== undefined && v !== null && v !== '')
        .map(([k, v]) => `${k}=${encodeURIComponent(v)}`)
        .join('&');
    return query ? `?${query}` : '';
};

export const transactionApi = {
    getAll(type, page = 0, size = 20) {
        const params = { type: type?.toUpperCase(), page, size };
        return httpClient.get(`${TRANSACTIONS_PATH}${buildQuery(params)}`);
    },

    getById(id) {
        return httpClient.get(`${TRANSACTIONS_PATH}/${id}`);
    },

    create(data) {
        return httpClient.post(TRANSACTIONS_PATH, { ...data, type: data.type?.toUpperCase() });
    },

    update(id, data) {
        return httpClient.put(`${TRANSACTIONS_PATH}/${id}`, { ...data, type: data.type?.toUpperCase() });
    },

    delete(id) {
        return httpClient.delete(`${TRANSACTIONS_PATH}/${id}`);
    },

    filter({
        type, description, category,
        startDate, endDate, minAmount, maxAmount,
        applicated, page = 0, size = 20
    } = {}) {
        const params = { type: type?.toUpperCase(), description, category, startDate, endDate, minAmount, maxAmount, applicated, page, size };
        return httpClient.get(`${TRANSACTIONS_PATH}/filters${buildQuery(params)}`);
    },

    deleteFiltered({
        type, description, category,
        startDate, endDate, minAmount, maxAmount
    } = {}) {
        const params = { type: type?.toUpperCase(), description, category, startDate, endDate, minAmount, maxAmount };
        return httpClient.delete(`${TRANSACTIONS_PATH}/delete-filtered${buildQuery(params)}`);
    }
};

export const balanceApi = {
    get() {
        return httpClient.get('/api/balance');
    },

    updateManual(totalBalance) {
        return httpClient.put('/api/balance/update-manual', { totalBalance });
    },

    updateAuto() {
        return httpClient.put('/api/balance/auto');
    }
};

export const exportApi = {
    transactions(type) {
        return httpClient.getBlob(`/api/export/${type?.toUpperCase()}/csv`);
    },

    filteredTransactions({
        type, description, category,
        startDate, endDate, minAmount, maxAmount
    }) {
        const params = { description, category, startDate, endDate, minAmount, maxAmount };
        return httpClient.getBlob(`/api/export/${type?.toUpperCase()}/filtered/csv${buildQuery(params)}`);
    },

    allTransactions() {
        return httpClient.getBlob('/api/export/all/transactions/csv');
    },

    allFilteredTransactions({
        description, category, startDate, endDate, minAmount, maxAmount
    }) {
        const params = { description, category, startDate, endDate, minAmount, maxAmount };
        return httpClient.getBlob(`/api/export/all/transactions/filtered/csv${buildQuery(params)}`);
    }
};