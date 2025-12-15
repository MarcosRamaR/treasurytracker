const API_EXPENSE_BASE = '/api/expenses'
const API_INCOMES_BASE = '/api/incomes'
const API_BALANCE_BASE = '/api/balance'
const API_EXPORT_BASE = '/api/export'
import { authService } from './authService.js'

const getAuthHeaders = () => {
    const token = authService.getToken()
    const headers = {
    'Content-Type': 'application/json'
    }
    if (token) {
        const authToken = token.startsWith('Bearer ') ? token : `Bearer ${token}`
        headers['Authorization'] = authToken
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
    getExpenseById: async (id) => { //Unused currently
        const response = await fetch(`${API_EXPENSE_BASE}/${id}`, {
            headers: getAuthHeaders()
        })
        return handleResponse(response)
    },
    getTotalExpenses: async () => {//Unused currently
        const response = await fetch(`${API_EXPENSE_BASE}/total`, {
            headers: getAuthHeaders()
        })
        return handleResponse(response)
    },
    getCurrentMonthExpenses: async () => {//Unused currently
        const response = await fetch(`${API_EXPENSE_BASE}/total/current-month`, {
            headers: getAuthHeaders()
        })
        return handleResponse(response)
    },
    getTotalExpensesByDateRange: async (startDate, endDate) => {//Unused currently
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
        const {description,category, startDate, endDate, minAmount, maxAmount} = filters
        const queryParams = [
            description ? `description=${encodeURIComponent(description)}` : '',
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
        console.log('Updating manual balance to:', newBalance)
        const response = await fetch(`${API_BALANCE_BASE}/update-manual`, {
            method: 'PUT',
            headers: getAuthHeaders(),
            body: JSON.stringify({totalBalance: newBalance}),
        })
        console.log('Response from updateManualBalance:', response)
        return handleResponse(response)
    },
    updateAutomaticBalance: async () => {
        const response = await fetch(`${API_BALANCE_BASE}/auto`, {
            method: 'PUT',
            headers: getAuthHeaders(),
        })
        return handleResponse(response)
    },
    exportAllTransactionsOneTypeToCsv: async (type) => {
        let transactionType = ''
        if(type === 'expense'){
            transactionType = 'expenses'
        }else if(type === 'income'){
            transactionType = 'incomes'
        }
        const response = await fetch(`${API_EXPORT_BASE}/${transactionType}/csv`, {
            headers: getAuthHeaders()
        })
        if (!response.ok) {
            if (response.status === 401) {
                authService.logout()
                window.location.reload()
            }
        throw new Error(`HTTP error! status: ${response.status}`)
        }
        //Blob response for file download, json will not work here
        return await response.blob()
    },
        exportFilteredTransactionsToCsv: async (filters,type) => {
        const {description,category, startDate, endDate, minAmount, maxAmount} = filters
        const queryParams = [
            description ? `description=${encodeURIComponent(description)}` : '',
            category ? `category=${category}` : '',
            startDate ? `startDate=${startDate}` : '',
            endDate ? `endDate=${endDate}` : '',
            minAmount ? `minAmount=${minAmount}` : '',
            maxAmount ? `maxAmount=${maxAmount}` : '',
        ]
        console.log('Filters for exportFilteredExpensesToCsv on api.js:', filters)
        const noNullParams = queryParams.filter(param => param !== '') 
        const queryStringParams = noNullParams.join('&') 
        
        let transactionUrl = ''
        if(type === 'expense'){
            transactionUrl = 'expenses'
        }else if(type === 'income'){
            transactionUrl = 'incomes'
        }else{
            throw new Error('Invalid type for filtering expenses')
        }
        const urlFiltered = queryStringParams ? `${API_EXPORT_BASE}/${transactionUrl}/filtered/csv?${queryStringParams}` : `${API_EXPORT_BASE}/${transactionUrl}/filtered/csv`
        console.log('Constructed URL for exportFilteredExpensesToCsv on api.js:', urlFiltered)
        const response = await fetch(urlFiltered, {
            headers: getAuthHeaders()
        })
        return await response.blob()
    },
    exportAllTransactionsToCsv: async () => {
        const response = await fetch(`${API_EXPORT_BASE}/all/transactions/csv`, {
            headers: getAuthHeaders()
        })
        if (!response.ok) {
            if (response.status === 401) {
                authService.logout()
                window.location.reload()
            }
        throw new Error(`HTTP error! status: ${response.status}`)
        }
        return await response.blob()
    },
        exportAllFilteredTransactionsToCsv: async (filters) => {
        const {description,category, startDate, endDate, minAmount, maxAmount} = filters
        const queryParams = [
            description ? `description=${encodeURIComponent(description)}` : '',
            category ? `category=${category}` : '',
            startDate ? `startDate=${startDate}` : '',
            endDate ? `endDate=${endDate}` : '',
            minAmount ? `minAmount=${minAmount}` : '',
            maxAmount ? `maxAmount=${maxAmount}` : '',
        ]
        console.log('Filters for exportFilteredExpensesToCsv on api.js:', filters)
        const noNullParams = queryParams.filter(param => param !== '') 
        const queryStringParams = noNullParams.join('&') 
        

        const urlFiltered = queryStringParams ? `${API_EXPORT_BASE}/all/transactions/filtered/csv?${queryStringParams}` : `${API_EXPORT_BASE}/all/transactions/filtered/csv`
        console.log('Constructed URL for exportFilteredExpensesToCsv on api.js:', urlFiltered)
        const response = await fetch(urlFiltered, {
            headers: getAuthHeaders()
        })
        return await response.blob()
    }

}