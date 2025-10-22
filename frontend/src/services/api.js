const API_EXPENSE_BASE = '/api/expenses'
const API_INCOMES_BASE = '/api/incomes'

export const apiService = {
    getAll: async (type) => {
        let newUrl= ''
        if(type === 'expense'){
            newUrl = API_EXPENSE_BASE
        }else if(type === ' income'){
            newUrl = API_INCOMES_BASE
        }else{
            throw new Error('Invalid type for getting all')
        }
        const response = await fetch(newUrl)
        if(!response.ok) throw new Error('Error fetching expenses')
        return response.json()
    },
    getExpenseById: async (id) => {
        const response = await fetch(`${API_EXPENSE_BASE}/${id}`)
        if(!response.ok) throw new Error('Error fetching expense')
        return response.json()
    },
    getTotalExpenses: async () => {
        const response = await fetch(`${API_EXPENSE_BASE}/total`)
        if(!response.ok) throw new Error('Error fetching total expenses')
        return response.json()
    },
    getCurrentMonthExpenses: async () => {
        const response = await fetch(`${API_EXPENSE_BASE}/total/current-month`)
        if(!response.ok) throw new Error('Error fetching current month expenses')
        return response.json()
    },
    getTotalExpensesByDateRange: async (startDate, endDate) => {
        const response = await fetch(`${API_EXPENSE_BASE}/total/date-range?startDate=${startDate}&endDate=${endDate}`)
        if(!response.ok) throw new Error('Error fetching total expenses by date range')
        return response.json()
    },
    create: async (data,type) => {

        let newUrl= ''
        if(type === 'expense'){
            newUrl = API_EXPENSE_BASE
        }else if(type === ' income'){
            newUrl = API_INCOMES_BASE
        }else{
            throw new Error('Invalid type for getting all')
        }
        const response = await fetch(newUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data),
            })
        if(!response.ok) throw new Error('Error posting expense')
        return response.json()
    },
    update: async (id, expense,type) => {
        let newUrl= ''
        if(type === 'expense'){
            newUrl = API_EXPENSE_BASE
        }else if(type === ' income'){
            newUrl = API_INCOMES_BASE
        }else{
            throw new Error('Invalid type for getting all')
        }
        const response = await fetch(`${newUrl}/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(expense),
        })
        if(!response.ok) throw new Error('Error updating expense')
        return response.json()
    },
    delete: async (id,type) => {
        let newUrl= ''
        if(type === 'expense'){
            newUrl = API_EXPENSE_BASE
        }else if(type === ' income'){
            newUrl = API_INCOMES_BASE
        }else{
            throw new Error('Invalid type for getting all')
        }
        const response = await fetch(`${newUrl}/${id}`, {
            method: 'DELETE',
    })
        if(!response.ok) throw new Error('Error deleting expense')
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
        }else if(type === ' income'){
            newUrl = API_INCOMES_BASE
        }else{
            throw new Error('Invalid type for filtering expenses')
        }
        const urlFiltered = queryStringParams ? `${newUrl}/filters?${queryStringParams}` : `${newUrl}/filters`
        const response = await fetch(urlFiltered)
        if(!response.ok) throw new Error('Error filtering expenses')
        return response.json()
    }

}