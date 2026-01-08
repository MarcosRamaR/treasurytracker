//Hook for expenses logic

import { useEffect, useState } from "react"
import { apiService } from "../services/api.js"
import { authService } from "../services/authService.js"

export const useExpenses = () => {
    const type = 'expense'
    const [expenses, setExpenses] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [filteredExpenses, setFilteredExpenses] = useState([])
    const [isFiltered, setIsFiltered] = useState(false)

    useEffect(() =>{
        loadExpenses()
    },[])

    const loadExpenses = async () => {
        try{
            setLoading(true)
                if (!authService.isAuthenticated()) {
                    setError('User not authenticated. Please log in.')
                    setExpenses([])
                    return
            }
            const data = await apiService.getAll(type)
            setExpenses(data)
            setError('')
        }catch(err){
            setError('Error loading expenses: ' + err.message)
        }finally{
            setLoading(false)
        }
    }

    const createExpense = async (expense) => {
        try{
            if (!authService.isAuthenticated()) {
                setError('User not authenticated. Please log in.')
                return
            }
            const  newExpense = await apiService.create(expense,type)
            setExpenses([...expenses, newExpense])
            setError('')
        }catch(err){
            setError('Error creating expense: ' + err.message)
        }
    }

    const updateExpense = async (id, expense) => {
        try{
            if (!authService.isAuthenticated()) {
                setError('User not authenticated. Please log in.')
                return
            }
            const updatedExpense = await apiService.update(id, expense,type)
            setExpenses(expenses.map(exp => exp.id === id ? updatedExpense : exp))
            setError('')
        }catch(err){
            setError('Error updating expense: ' + err.message)
        }
    }

    const deleteExpense = async (id) => {
        try{
            if (!authService.isAuthenticated()) {
                setError('User not authenticated. Please log in.')
                return
            }
            await apiService.delete(id,type)
            setExpenses(expenses.filter(item => item.id !== id))
            setError('')
        }catch(err){
            setError('Error deleting expense: ' + err.message)
        }
    }

    const filterExpenses = async (filters) => {
        try{
            if (!authService.isAuthenticated()) {
                setError('User not authenticated. Please log in.')
                return
            }
            const filtered = await apiService.filterExpenses(filters,type)
            setFilteredExpenses(filtered)
            setIsFiltered(true)
            setError('')
        }catch(err){
            setError('Error filtering expenses: ' + err.message)
            throw err
        }
    }

    const clearFilters = () => {
        setIsFiltered(false)
        setFilteredExpenses([])
        setError('')
    }

  return {
    expenses: isFiltered ? filteredExpenses : expenses,
    loading,
    error,
    isFiltered,
    loadExpenses,
    createExpense,
    updateExpense,
    deleteExpense,
    filterExpenses,
    clearFilters
  }
}
