const API_BASE = 'http://localhost:8080/api/expenses'

export const expenseService = {
    getAll: async () => {
        const response = await fetch(API_BASE)
        if(!response.ok) throw new Error('Error fetching expenses')
        return response.json()
    },
    getExpenseById: async (id) => {
        const response = await fetch(`${API_BASE}/${id}`)
        if(!response.ok) throw new Error('Error fetching expense')
        return response.json()
    },
    getTotalExpenses: async () => {
        const response = await fetch(`${API_BASE}/total`)
        if(!response.ok) throw new Error('Error fetching total expenses')
        return response.json()
    },
    getExpensesByCategory: async (category) => {
        const response = await fetch(`${API_BASE}/category/${category}`)
        if(!response.ok) throw new Error('Error fetching expenses by category')
        return response.json()
    },
    getCurrentMonthExpenses: async () => {
        const response = await fetch(`${API_BASE}/total/current-month`)
        if(!response.ok) throw new Error('Error fetching current month expenses')
        return response.json()
    },
    getExpensesByDateRange: async (startDate, endDate) => {
        const response = await fetch(`${API_BASE}/between-date?start=${startDate}&end=${endDate}`)
        if(!response.ok) throw new Error('Error fetching expenses by date range')
        return response.json()
    },
    getTotalExpensesByDateRange: async (startDate, endDate) => {
        const response = await fetch(`${API_BASE}/total/date-range?start=${startDate}&end=${endDate}`)
        if(!response.ok) throw new Error('Error fetching total expenses by date range')
        return response.json()
    },
    getExpensesGraterThan: async (amount) => {
        const response = await fetch(`${API_BASE}/amount-greater-than/${amount}`)
        if(!response.ok) throw new Error('Error fetching expenses greater than amount')
        return response.json()
    },
    getExpensesByCategoryAndDateRange: async (category, startDate, endDate) => {
        const response = await fetch(`${API_BASE}/category/${category}/date-range?start=${startDate}&end=${endDate}`)
        if(!response.ok) throw new Error('Error fetching expenses by category and date range')
        return response.json()
    },
    create: async (expense) => {
        const response = await fetch(API_BASE, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(expense),
            })
        if(!response.ok) throw new Error('Error posting expense')
        return response.json()
    },
    update: async (id, expense) => {
        const response = await fetch(`${API_BASE}/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(expense),
        })
        if(!response.ok) throw new Error('Error updating expense')
        return response.json()
    },
    delete: async (id) => {
        const response = await fetch(`${API_BASE}/${id}`, {
            method: 'DELETE',
    })
        if(!response.ok) throw new Error('Error deleting expense')
        return true
    }

}