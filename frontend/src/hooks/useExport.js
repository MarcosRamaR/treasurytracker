import { apiService } from '../services/api.js'
import { authService } from '../services/authService'
import { useState } from 'react'


export const useExport = () => {
const [error, setError] = useState('')
const [exporting, setExporting] = useState(false)

const downloadBlob = (blob, filename) => {
    const url = window.URL.createObjectURL(blob) // Create a URL for the blob on memory
    // Create a temporary anchor element to trigger the download
    const a = document.createElement('a')
    a.style.display = 'none'
    a.href = url
    a.download = filename
    // Append to body and trigger click
    document.body.appendChild(a)
    a.click()
    // Clean up the URL object and remove the anchor
    window.URL.revokeObjectURL(url)
    document.body.removeChild(a)
}

const exportAllExpensesToCsv = async () => {
    try {
        setExporting(true)
        setError('')
        if (!authService.isAuthenticated()) {
            setError('User not authenticated. Please log in.')
            return
        }
        // Call the API to get the CSV blob
        const blob = await apiService.exportAllExpensesToCsv()
        
        const filename = `expenses_${new Date().toISOString().split('T')[0]}.csv`
        downloadBlob(blob, filename)
    } catch (err) {
        console.error('Error exporting expenses to CSV:', err)
        setError('Failed to export expenses: ' + err.message)
        throw err
    }finally{
        setExporting(false)
    }
}
return{
    exportAllExpensesToCsv,
    exporting,
    error
}


}