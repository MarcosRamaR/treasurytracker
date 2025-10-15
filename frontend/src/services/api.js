const API_BASE = 'http://localhost:8080/api/expenses'

export const expenseService = {
    getAll: async () => {
        const response = await fetch(API_BASE)
        if(!response.ok) throw new Error('Error fetching expenses')
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