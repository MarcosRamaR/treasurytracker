const API_EXPENSE_BASE = '/api/expenses'
const API_INCOMES_BASE = '/api/incomes'
const API_BALANCE_BASE = '/api/balance'
import { authService } from './authService.js'

const getAuthHeaders = () => {
    const token = authService.getToken()
    const headers = {
    'Content-Type': 'application/json'
    }
    if (token) {
    headers['Authorization'] = token
    }
    console.log('Headers:', headers)
    return headers
}

const handleResponse = async (response) => {
    if (!response.ok) {
    if (response.status === 401) {
        authService.logout()
        window.location.reload()
    }
    throw new Error(`HTTP error! status: ${response.status}`)
    }
    return response.json()
}

export const apiService = {
    getAll: async (type) => {
        let newUrl= ''
        if(type === 'expense'){
            newUrl = API_EXPENSE_BASE
        }else if(type === 'income'){
            newUrl = API_INCOMES_BASE
        }else{
            throw new Error('Invalid type for getting all')
        }
        const response = await fetch(newUrl, {
            headers: getAuthHeaders()
        })
        return handleResponse(response)
    },
    getExpenseById: async (id) => {
        const response = await fetch(`${API_EXPENSE_BASE}/${id}`, {
            headers: getAuthHeaders()
        })
        return handleResponse(response)
    },
    getTotalExpenses: async () => {
        const response = await fetch(`${API_EXPENSE_BASE}/total`, {
            headers: getAuthHeaders()
        })
        return handleResponse(response)
    },
    getCurrentMonthExpenses: async () => {
        const response = await fetch(`${API_EXPENSE_BASE}/total/current-month`, {
            headers: getAuthHeaders()
        })
        return handleResponse(response)
    },
    getTotalExpensesByDateRange: async (startDate, endDate) => {
        const response = await fetch(
            `${API_EXPENSE_BASE}/total/date-range?startDate=${startDate}&endDate=${endDate}`, 
            { headers: getAuthHeaders() }
        )
        return handleResponse(response)
    },
    create: async (data,type) => {
        let newUrl= ''
        if(type === 'expense'){
            newUrl = API_EXPENSE_BASE
        }else if(type === 'income'){
            newUrl = API_INCOMES_BASE
        }else{
            throw new Error('Invalid type for getting all')
        }
        const response = await fetch(newUrl, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(data),
        })
        return handleResponse(response)
    },
    update: async (id, expense,type) => {
        let newUrl= ''
        if(type === 'expense'){
            newUrl = API_EXPENSE_BASE
        }else if(type === 'income'){
            newUrl = API_INCOMES_BASE
        }else{
            throw new Error('Invalid type for getting all')
        }
        const response = await fetch(`${newUrl}/${id}`, {
            method: 'PUT',
            headers: getAuthHeaders(),
            body: JSON.stringify(expense),
        })
        return handleResponse(response)
    },
    delete: async (id,type) => {
        let newUrl= ''
        if(type === 'expense'){
            newUrl = API_EXPENSE_BASE
        }else if(type === 'income'){
            newUrl = API_INCOMES_BASE
        }else{
            throw new Error('Invalid type for getting all')
        }
        const response = await fetch(`${newUrl}/${id}`, {
            method: 'DELETE',
            headers: getAuthHeaders(),
        })
        
        if (!response.ok) {
            if (response.status === 401) {
                authService.logout()
                window.location.reload()
            }
            throw new Error(`HTTP error! status: ${response.status}`)
        }
        
        return true
    },
    filterExpenses: async (filters,type) => {
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

        let newUrl = ''
        if(type === 'expense'){
            newUrl = API_EXPENSE_BASE
        }else if(type === 'income'){
            newUrl = API_INCOMES_BASE
        }else{
            throw new Error('Invalid type for filtering expenses')
        }
        const urlFiltered = queryStringParams ? `${newUrl}/filters?${queryStringParams}` : `${newUrl}/filters`
        const response = await fetch(urlFiltered, {
            headers: getAuthHeaders()
        })
        return handleResponse(response)
    },
    getBalance: async () => {
        const response = await fetch(`${API_BALANCE_BASE}`, {
            headers: getAuthHeaders()
        })
        return handleResponse(response)
    },
    updateManualBalance: async (newBalance) => {
        const response = await fetch(`${API_BALANCE_BASE}/manual`, {
            method: 'PUT',
            headers: getAuthHeaders(),
            body: JSON.stringify({amount: newBalance}),
        })
        return handleResponse(response)
    },
    updateAutomaticBalance: async () => {
        const response = await fetch(`${API_BALANCE_BASE}/auto`, {
            method: 'PUT',
            headers: getAuthHeaders(),
        })
        return handleResponse(response)
    }

}