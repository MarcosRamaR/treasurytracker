//Hook for expenses logic

import { useEffect, useState } from "react"
import { expenseService } from "../services/api.js"

export const useExpenses = () => {

    const [expenses, setExpenses] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

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

  return {
    expenses,
    loading,
    error,
    loadExpenses,
    createExpense,
    updateExpense,
    deleteExpense
  }
}
