const API_BASE_URL = import.meta.env.VITE_API_AUTH_BASE
    ? `${import.meta.env.VITE_API_AUTH_BASE}/api/auth`
    : 'http://localhost:8081/api/auth';

let refreshPromise = null;

const getToken = () => localStorage.getItem('token');
const getRefreshToken = () => localStorage.getItem('refreshToken');
const setTokens = (accessToken, refreshToken) => {
    localStorage.setItem('token', accessToken);
    if (refreshToken) localStorage.setItem('refreshToken', refreshToken);
};
const clearTokens = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
};

const tryRefreshToken = async () => {
    const refreshToken = getRefreshToken();
    if (!refreshToken) return false;

    if (refreshPromise) return refreshPromise;

    refreshPromise = (async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/refresh`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ refreshToken })
            });
            if (response.ok) {
                const data = await response.json();
                setTokens(data.accessToken, data.refreshToken);
                return true;
            }
            return false;
        } catch {
            return false;
        } finally {
            refreshPromise = null;
        }
    })();

    return refreshPromise;
};

export const authService = {
    async register(userData) {
        const response = await fetch(`${API_BASE_URL}/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });
        if (!response.ok) {
            const error = await response.json().catch(() => ({}));
            throw new Error(error.message || `Registration failed (${response.status})`);
        }
        return response.json();
    },

    async login(credentials) {
        const response = await fetch(`${API_BASE_URL}/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(credentials)
        });

        if (!response.ok) {
            const error = await response.json().catch(() => ({}));
            throw new Error(error.message || 'Invalid email or password');
        }

        const data = await response.json();
        setTokens(data.accessToken, data.refreshToken);

        this.saveUserData({
            email: data.email,
            username: data.userName,
            id: data.userId
        });

        return data;
    },

    saveUserData(userData) {
        localStorage.setItem('user', JSON.stringify(userData));
    },

    logout() {
        clearTokens();
    },

    getAuthHeaders() {
        const token = getToken();
        return token ? { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } : { 'Content-Type': 'application/json' };
    },

    isAuthenticated() {
        return !!getToken();
    },

    getToken,
    getRefreshToken,

    async fetchWithAuth(url, options = {}) {
        const headers = { ...this.getAuthHeaders(), ...options.headers };
        let response = await fetch(url, { ...options, headers });

        if (response.status === 401 && getRefreshToken()) {
            const refreshed = await tryRefreshToken();
            if (refreshed) {
                const retryHeaders = { ...this.getAuthHeaders(), ...options.headers };
                response = await fetch(url, { ...options, headers: retryHeaders });
            } else {
                this.logout();
                window.location.reload();
            }
        }

        return response;
    },

    getUser() {
        const user = localStorage.getItem('user');
        return user ? JSON.parse(user) : null;
    }
};