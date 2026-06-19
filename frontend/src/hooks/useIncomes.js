import { useTransactions } from './useTransactions'

export const useIncomes = () => {
    console.warn('useIncomes is deprecated. Use useTransactions(\'income\') instead.')
    return useTransactions('income')
}