const API_BASE = '/api/expenses'

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
    getCurrentMonthExpenses: async () => {
        const response = await fetch(`${API_BASE}/total/current-month`)
        if(!response.ok) throw new Error('Error fetching current month expenses')
        return response.json()
    },
    getTotalExpensesByDateRange: async (startDate, endDate) => {
        const response = await fetch(`${API_BASE}/total/date-range?startDate=${startDate}&endDate=${endDate}`)
        if(!response.ok) throw new Error('Error fetching total expenses by date range')
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
    },
    filterExpenses: async (filters) => {
        const {category, startDate, endDate, minAmount, maxAmount} = filters
        const queryParams = [
            category ? `category=${category}` : '',
            startDate ? `startDate=${startDate}` : '',
            endDate ? `endDate=${endDate}` : '',
            minAmount ? `minAmount=${minAmount}` : '',
            maxAmount ? `maxAmount=${maxAmount}` : '',
        ] //Each filter is optional and assigned only if exists
        const noNullParams = queryParams.filter(param => param !== '') //remove empty params
        const queryStringParams = noNullParams.join('&') //join with &

        const urlFiltered = queryStringParams ? `${API_BASE}/filters?${queryStringParams}` : `${API_BASE}/filters`
        const response = await fetch(urlFiltered)
        if(!response.ok) throw new Error('Error filtering expenses')
        return response.json()
    }

}