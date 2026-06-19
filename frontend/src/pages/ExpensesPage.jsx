import { ExpenseForm } from "../components/expenses/ExpenseForm"
import { ExpenseList } from "../components/expenses/ExpenseList"
import { FilterSection } from "../components/FilterSection"
import { useTransactions } from "../hooks/useTransactions"
import { useState } from "react"
import { ExpenseEdit } from "../components/expenses/ExpenseEdit"
import { Spinner } from "../components/Spinner"
import '../styles/ExpensesStyle.css'

export function ExpensesPage() {
    const {transactions, loading, error, isFiltered,
        loadTransactions, createTransaction, updateTransaction, deleteTransaction,
        filterTransactions, clearFilters, deleteFilteredTransactions
    } = useTransactions('expense')

    const [editExpense, setEditExpense] = useState(null)
    const [isEditModalOpen, setIsEditModalOpen] = useState(false)
    const [fieldDescription, setFieldDescription] = useState('')
    const [startDate, setStartDate] = useState('')
    const [endDate, setEndDate] = useState('')
    const [categorySelect, setCategorySelect] = useState('')
    const [minAmount, setMinAmount] = useState('')
    const [maxAmount, setMaxAmount] = useState('')
    const [currentFilters, setCurrentFilters] = useState({})
    const [filterLoading, setFilterLoading] = useState(false)

    const categories = ['Food', 'Transport', 'Entertainment', 'Others']

    const handleAddExpense = async (expense) => {
        await createTransaction(expense)
    }

    const handleDeleteExpense = async (id) => {
        await deleteTransaction(id)
        await loadTransactions()
    }

    const handleEditExpense = (expense) => {
        setEditExpense(expense)
        setIsEditModalOpen(true)
    }

    const handleUpdateExpense = async (expense) => {
        if (!expense || !editExpense) return
        await updateTransaction(editExpense.id, expense)
        setIsEditModalOpen(false)
        setEditExpense(null)
    }

    const handleCloseModal = () => {
        setIsEditModalOpen(false)
        setEditExpense(null)
    }

    const handleFilters = async () => {
        const filters = {
            description: fieldDescription,
            category: categorySelect,
            startDate: startDate,
            endDate: endDate,
            minAmount: minAmount ? parseFloat(minAmount) : undefined,
            maxAmount: maxAmount ? parseFloat(maxAmount) : undefined
        }
        setCurrentFilters(filters)
        setFilterLoading(true)
        try {
            await filterTransactions(filters)
        } finally {
            setFilterLoading(false)
        }
    }

    const handleDeleteFilteredTransactions = async () => {
        const filters = {
            description: fieldDescription,
            category: categorySelect,
            startDate: startDate,
            endDate: endDate,
            minAmount: minAmount ? parseFloat(minAmount) : undefined,
            maxAmount: maxAmount ? parseFloat(maxAmount) : undefined
        }
        setCurrentFilters(filters)
        setFilterLoading(true)
        try {
            await deleteFilteredTransactions(filters)
        } finally {
            setFilterLoading(false)
        }
    }

    const handleClearFilters = () => {
        clearFilters()
        setFieldDescription('')
        setCategorySelect('')
        setStartDate('')
        setEndDate('')
        setMinAmount('')
        setMaxAmount('')
        setFilterLoading(false)
    }

    if (loading && transactions.length === 0) return <div><Spinner /></div>
    if (error && transactions.length === 0) return <div>Error: {error}</div>

    return (
        <>
            <h2>Expenses Page</h2>
            <ExpenseForm onSubmit={handleAddExpense} />
            <ExpenseEdit
                expense={editExpense}
                isOpen={isEditModalOpen}
                onClose={handleCloseModal}
                onSubmit={handleUpdateExpense}
            />
            <FilterSection
                fieldDescription={fieldDescription}
                setFieldDescription={setFieldDescription}
                startDate={startDate}
                setStartDate={setStartDate}
                endDate={endDate}
                setEndDate={setEndDate}
                categorySelect={categorySelect}
                setCategorySelect={setCategorySelect}
                minAmount={minAmount}
                setMinAmount={setMinAmount}
                maxAmount={maxAmount}
                setMaxAmount={setMaxAmount}
                categories={categories}
                onFilter={handleFilters}
                onClearFilters={handleClearFilters}
                onDeleteFilteredTransactions={handleDeleteFilteredTransactions}
                isFiltered={isFiltered}
            />
            {filterLoading ? (
                <div><Spinner /></div>
            ) : (
                <ExpenseList
                    expenses={transactions}
                    onDelete={handleDeleteExpense}
                    onEdit={handleEditExpense}
                    currentFilters={currentFilters}
                />
            )}
        </>
    )
}