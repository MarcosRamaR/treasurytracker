import { useState, useEffect, useCallback, useRef } from 'react';
import { transactionApi } from '../services/transactionApi';
import { authService } from '../services/authService';

export const useTransactions = (type) => {
    const [transactions, setTransactions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [filteredTransactions, setFilteredTransactions] = useState([]);
    const [isFiltered, setIsFiltered] = useState(false);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const mountedRef = useRef(true);
    const abortControllerRef = useRef(null);

    useEffect(() => {
        mountedRef.current = true;
        return () => {
            mountedRef.current = false;
            if (abortControllerRef.current) abortControllerRef.current.abort();
        };
    }, []);

    const safeSetState = useCallback((setter, value) => {
        if (mountedRef.current) setter(value);
    }, []);

    const loadTransactions = useCallback(async (pageNum = 0) => {
        if (abortControllerRef.current) abortControllerRef.current.abort();
        abortControllerRef.current = new AbortController();

        safeSetState(setLoading, true);
        try {
            if (!authService.isAuthenticated()) {
                safeSetState(setError, 'User not authenticated. Please log in.');
                safeSetState(setTransactions, []);
                return;
            }
            const response = await transactionApi.getAll(type, pageNum);
            if (mountedRef.current) {
                setTransactions(response.content || []);
                setTotalPages(response.totalPages || 0);
                setTotalElements(response.totalElements || 0);
                setPage(pageNum);
                safeSetState(setError, '');
            }
        } catch (err) {
            safeSetState(setError, `Error loading ${type}s: ${err.message}`);
        } finally {
            safeSetState(setLoading, false);
        }
    }, [type, safeSetState]);

    useEffect(() => {
        loadTransactions(0);
    }, [loadTransactions]);

    const createTransaction = useCallback(async (data) => {
        try {
            if (!authService.isAuthenticated()) {
                safeSetState(setError, 'User not authenticated. Please log in.');
                return;
            }
            const newTransaction = await transactionApi.create({ ...data, type });
            if (mountedRef.current) {
                setTransactions(prev => [newTransaction, ...prev]);
                safeSetState(setError, '');
            }
        } catch (err) {
            safeSetState(setError, `Error creating ${type}: ${err.message}`);
        }
    }, [type, safeSetState]);

    const updateTransaction = useCallback(async (id, data) => {
        try {
            if (!authService.isAuthenticated()) {
                safeSetState(setError, 'User not authenticated. Please log in.');
                return;
            }
            const updated = await transactionApi.update(id, { ...data, type });
            if (mountedRef.current) {
                setTransactions(prev => prev.map(t => t.id === id ? updated : t));
                safeSetState(setError, '');
            }
        } catch (err) {
            safeSetState(setError, `Error updating ${type}: ${err.message}`);
        }
    }, [type, safeSetState]);

    const deleteTransaction = useCallback(async (id) => {
        try {
            if (!authService.isAuthenticated()) {
                safeSetState(setError, 'User not authenticated. Please log in.');
                return;
            }
            await transactionApi.delete(id);
            if (mountedRef.current) {
                setTransactions(prev => prev.filter(t => t.id !== id));
                safeSetState(setError, '');
            }
        } catch (err) {
            safeSetState(setError, `Error deleting ${type}: ${err.message}`);
        }
    }, [type, safeSetState]);

    const filterTransactions = useCallback(async (filters) => {
        try {
            if (!authService.isAuthenticated()) {
                safeSetState(setError, 'User not authenticated. Please log in.');
                return;
            }
            const response = await transactionApi.filter({ ...filters, type });
            if (mountedRef.current) {
                setFilteredTransactions(response.content || []);
                setTotalPages(response.totalPages || 0);
                setTotalElements(response.totalElements || 0);
                setIsFiltered(true);
                safeSetState(setError, '');
            }
        } catch (err) {
            safeSetState(setError, `Error filtering ${type}s: ${err.message}`);
            throw err;
        }
    }, [type, safeSetState]);

    const deleteFilteredTransactions = useCallback(async (filters) => {
        try {
            if (!authService.isAuthenticated()) {
                safeSetState(setError, 'User not authenticated. Please log in.');
                return;
            }
            await transactionApi.deleteFiltered({ ...filters, type });
            await loadTransactions(0);
            clearFilters();
        } catch (err) {
            safeSetState(setError, `Error deleting filtered ${type}s: ${err.message}`);
            throw err;
        }
    }, [type, loadTransactions, safeSetState]);

    const clearFilters = useCallback(() => {
        safeSetState(setIsFiltered, false);
        safeSetState(setFilteredTransactions, []);
        safeSetState(setError, '');
    }, [safeSetState]);

    return {
        transactions: isFiltered ? filteredTransactions : transactions,
        loading,
        error,
        isFiltered,
        page,
        totalPages,
        totalElements,
        loadTransactions,
        createTransaction,
        updateTransaction,
        deleteTransaction,
        filterTransactions,
        deleteFilteredTransactions,
        clearFilters
    };
};