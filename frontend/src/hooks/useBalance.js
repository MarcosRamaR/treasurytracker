import { useState, useEffect, useCallback, useRef } from 'react'
import { balanceApi } from '../services/transactionApi'
import { authService } from '../services/authService'

export const useBalance = () => {
    const [balance, setBalance] = useState(0)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const mountedRef = useRef(true)

    useEffect(() => {
        mountedRef.current = true
        return () => { mountedRef.current = false }
    }, [])

    const loadBalance = useCallback(async () => {
        try {
            setLoading(true)
            if (!authService.isAuthenticated()) {
                setError('User not authenticated. Please log in.')
                setBalance(0)
                return
            }
            const data = await balanceApi.get()
            if (mountedRef.current) {
                setBalance(data.totalBalance || 0)
                setError('')
            }
        } catch (err) {
            if (mountedRef.current) setError('Error loading balance: ' + err.message)
        } finally {
            if (mountedRef.current) setLoading(false)
        }
    }, [])

    useEffect(() => { loadBalance() }, [loadBalance])

    const updateManualBalance = useCallback(async (newBalance) => {
        try {
            setLoading(true)
            if (!authService.isAuthenticated()) {
                setError('User not authenticated. Please log in.')
                return
            }
            const data = await balanceApi.updateManual(newBalance)
            if (mountedRef.current) {
                setBalance(data.totalBalance)
                setError('')
            }
        } catch (err) {
            if (mountedRef.current) setError('Error updating balance: ' + err.message)
        } finally {
            if (mountedRef.current) setLoading(false)
        }
    }, [])

    const updateAutomaticBalance = useCallback(async () => {
        try {
            setLoading(true)
            if (!authService.isAuthenticated()) {
                setError('User not authenticated. Please log in.')
                return
            }
            const data = await balanceApi.updateAuto()
            if (mountedRef.current) {
                setBalance(data.totalBalance || 0)
                setError('')
            }
        } catch (err) {
            if (mountedRef.current) setError('Error updating balance: ' + err.message)
        } finally {
            if (mountedRef.current) setLoading(false)
        }
    }, [])

    return {
        balance,
        loading,
        error,
        loadBalance,
        updateManualBalance,
        updateAutomaticBalance
    }
}