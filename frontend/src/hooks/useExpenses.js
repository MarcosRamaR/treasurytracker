//Hook for expenses logic

import { useEffect, useState } from "react"
import { expenseService } from "../services/api.js"

export const useExpenses = () => {

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
            const data = await expenseService.getAll()
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
            const  newExpense = await expenseService.create(expense)
            setExpenses([...expenses, newExpense])
            setError('')
        }catch(err){
            setError('Error creating expense: ' + err.message)
        }
    }

    const updateExpense = async (id, expense) => {
        try{
            const updatedExpense = await expenseService.update(id, expense)
            setExpenses(expenses.map(exp => exp.id === id ? updatedExpense : exp))
            setError('')
        }catch(err){
            setError('Error updating expense: ' + err.message)
        }
    }

    const deleteExpense = async (id) => {
        try{
            await expenseService.delete(id)
            setExpenses(expenses.filter(item => item.id !== id))
            setError('')
        }catch(err){
            setError('Error deleting expense: ' + err.message)
        }
    }


    const filterExpenses = async (filters) => {
        try{
            setLoading(true)
            const filtered = await expenseService.filterExpenses(filters)
            setFilteredExpenses(filtered)
            setIsFiltered(true)
            setError('')
        }catch(err){
            setError('Error filtering expenses: ' + err.message)
            throw err
        }finally{
            setLoading(false)
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
