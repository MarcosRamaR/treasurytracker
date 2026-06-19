import { exportApi } from '../services/transactionApi'
import { authService } from '../services/authService'
import { useState } from 'react'

export const useExport = () => {
    const [error, setError] = useState('')
    const [exporting, setExporting] = useState(false)

    const downloadBlob = (blob, filename) => {
        const url = window.URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.style.display = 'none'
        a.href = url
        a.download = filename
        document.body.appendChild(a)
        a.click()
        window.URL.revokeObjectURL(url)
        document.body.removeChild(a)
    }

    const exportTransactionsToCsv = async (type) => {
        try {
            setExporting(true)
            setError('')
            if (!authService.isAuthenticated()) {
                setError('User not authenticated. Please log in.')
                return
            }
            const blob = await exportApi.transactions(type)
            const filename = `${type}_${new Date().toISOString().split('T')[0]}.csv`
            downloadBlob(blob, filename)
        } catch (err) {
            setError('Failed to export transactions: ' + err.message)
            throw err
        } finally {
            setExporting(false)
        }
    }

    const exportFilteredTransactionsToCsv = async (filters, type) => {
        try {
            setExporting(true)
            setError('')
            if (!authService.isAuthenticated()) {
                setError('User not authenticated. Please log in.')
                return
            }
            const blob = await exportApi.filteredTransactions({ ...filters, type })
            const filename = `${type}_filtered_${new Date().toISOString().split('T')[0]}.csv`
            downloadBlob(blob, filename)
        } catch (err) {
            setError('Failed to export filtered transactions: ' + err.message)
            throw err
        } finally {
            setExporting(false)
        }
    }

    const exportAllTransactionsToCsv = async () => {
        try {
            setExporting(true)
            setError('')
            if (!authService.isAuthenticated()) {
                setError('User not authenticated. Please log in.')
                return
            }
            const blob = await exportApi.allTransactions()
            const filename = `all_transactions_${new Date().toISOString().split('T')[0]}.csv`
            downloadBlob(blob, filename)
        } catch (err) {
            setError('Failed to export transactions: ' + err.message)
            throw err
        } finally {
            setExporting(false)
        }
    }

    const exportAllFilteredTransactionsToCsv = async (filters) => {
        try {
            setExporting(true)
            setError('')
            if (!authService.isAuthenticated()) {
                setError('User not authenticated. Please log in.')
                return
            }
            const blob = await exportApi.allFilteredTransactions(filters)
            const filename = `all_filtered_transactions_${new Date().toISOString().split('T')[0]}.csv`
            downloadBlob(blob, filename)
        } catch (err) {
            setError('Failed to export filtered transactions: ' + err.message)
            throw err
        } finally {
            setExporting(false)
        }
    }

    return {
        exportAllTransactionsOneTypeToCsv: exportTransactionsToCsv,
        exportFilteredTransactionsToCsv,
        exportAllTransactionsToCsv,
        exportAllFilteredTransactionsToCsv,
        exportTransactionsToCsv,
        exporting,
        error
    }
}