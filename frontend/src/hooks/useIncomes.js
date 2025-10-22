import { useEffect, useState } from "react"
import { apiService } from "../services/api.js"


export const useIncomes = () => {
    const type = 'income'
    const [incomes, setincomes] = useState([])
    const [loading, setLoading] = useState([])
    const [error, setError] = useState([])
    const [filteredIncomes, setFilteredIncomes] = useState([])
    const [isfiltered, setisFiltered] = useState([])

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
            const  newIncome = await apiService.create(income)
            setincomes([...incomes, newIncome])
            setError('')
        }catch(err){
            setError('Error creating income: ' + err.message)
        }
    }

  return {
    incomes,
    loading,
    error,
    isfiltered,
    createIncome
  }
}
