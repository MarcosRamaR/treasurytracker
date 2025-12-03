import {apiService} from '../services/api.js'
import { useState, useEffect } from 'react'
import { authService } from '../services/authService.js'

export const useBalance = () => {
    const [balance, setBalance] = useState(0)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('') 
    useEffect(() => {
        loadBalance()
    }, [])

    const loadBalance = async () => {
        try{
            setLoading(true)
            if (!authService.isAuthenticated()) {
                setError('User not authenticated. Please log in.')
                setBalance(0)
                return
            }
            const data = await apiService.getBalance()
            console.log('Balance data:', data)
            setBalance(data.totalBalance)
            setError('')
        }catch(err){
            setError('Error loading balance: ' + err.message)
        }finally{
            setLoading(false)
        }
    }

    const updateManualBalance = async (newBalance) => {
                try{
            setLoading(true)
            if (!authService.isAuthenticated()) {
                setError('User not authenticated. Please log in.')
                setBalance(0)
                return
            }
        const updatedBalance = await apiService.updateManualBalance(newBalance)
        console.log('API response for manual balance update:', updatedBalance)
        setBalance(updatedBalance.amount)
        console.log('Updated balance:', updatedBalance)
        setError('')
        }catch(err){
            setError('Error loading balance: ' + err.message)
        }finally{
            setLoading(false)
        }
    }

    const updateAutomaticBalance = async () => {
                try{
            setLoading(true)
            if (!authService.isAuthenticated()) {
                setError('User not authenticated. Please log in.')
                setBalance(0)
                return
            }
        const updatedBalance = await apiService.updateAutomaticBalance()
        setBalance(updatedBalance)
        setError('')
        }catch(err){
            setError('Error loading balance: ' + err.message)
        }finally{
            setLoading(false)
        }
    }

    return {
        balance, 
        loading, 
        error, 
        loadBalance, 
        updateManualBalance, 
        updateAutomaticBalance}
}