import { useTransactions } from './useTransactions'

export const useExpenses = () => {
    console.warn('useExpenses is deprecated. Use useTransactions(\'expense\') instead.')
    return useTransactions('expense')
}