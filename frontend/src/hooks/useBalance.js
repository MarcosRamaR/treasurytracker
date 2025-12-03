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
        console.log('Updating manual balance to/On updateManualBalance):', newBalance)
                try{
            setLoading(true)
            if (!authService.isAuthenticated()) {
                setError('User not authenticated. Please log in.')
                setBalance(0)
                return
            }
        const updatedBalance = await apiService.updateManualBalance(newBalance)
        console.log('Updated balance data:', updatedBalance)
        setBalance(updatedBalance.totalBalance)
        console.log('Balance after update:', updatedBalance.totalBalance)
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