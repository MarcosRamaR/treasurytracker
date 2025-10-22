import { useEffect, useState } from "react"
import { apiService } from "../services/api.js"


export const useIncomes = () => {
    const type = 'income'
    const [incomes, setincomes] = useState([])
    const [loading, setLoading] = useState([])
    const [error, setError] = useState([])
    const [filteredIncomes, setFilteredIncomes] = useState([])
    const [isfiltered, setIsFiltered] = useState(false)

    useEffect(() =>{
        loadIncomes()
    },[])

    const loadIncomes = async () => {
        try{
            setLoading(true)
            const data = await apiService.getAll(type)
            setincomes(data)
            setError('')
        }catch(err){
            setError('Error loading incomes: ' + err.message)
        }finally{
            setLoading(false)
        }
    }

    const createIncome = async (income) => {
        try{
            const  newIncome = await apiService.create(income,type)
            setincomes([...incomes, newIncome])
            setError('')
        }catch(err){
            setError('Error creating income: ' + err.message)
        }
    }

    const updateIncome = async (id, income) => {
        try{
            const updatedIncome = await apiService.update(id, income,type)
            setincomes(incomes.map(inc => inc.id === id ? updatedIncome : inc))
            setError('')
        }catch(err){
            setError('Error updating income: ' + err.message)
        }
    }

    const deleteIncome = async (id) => {
        try{
            await apiService.delete(id,type)
            setincomes(incomes.filter(item => item.id !== id))
            setError('')
        }catch(err){
            setError('Error deleting income: ' + err.message)
        }
    }

    const filterIncomes = async (filters) => {
        try{
            setLoading(true)
            const filtered = await apiService.filterExpenses(filters,type)
            setFilteredIncomes(filtered)
            setIsFiltered(true)
            setError('')
        }catch(err){
            setError('Error filtering incomes: ' + err.message)
            throw err
        }finally{
            setLoading(false)
        }
    }
    const clearFilters = () => {
        setIsFiltered(false)
        setFilteredIncomes([])
        setError('')
    }


  return {
    incomes: isfiltered ? filteredIncomes : incomes,
    loading,
    error,
    isfiltered,
    createIncome,
    updateIncome,
    deleteIncome,
    filterIncomes,
    clearFilters
  }
}
