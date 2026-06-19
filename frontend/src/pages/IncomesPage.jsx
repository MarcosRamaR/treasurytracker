import { IncomeForm } from "../components/incomes/IncomeForm"
import { IncomeList } from "../components/incomes/IncomeList"
import { FilterSection } from "../components/FilterSection"
import { useTransactions } from "../hooks/useTransactions"
import { useState } from "react"
import { IncomeEdit } from "../components/incomes/IncomeEdit"
import { Spinner } from "../components/Spinner"
import '../styles/ExpensesStyle.css'

export function IncomesPage() {
    const {transactions, loading, error, isFiltered,
        loadTransactions, createTransaction, updateTransaction, deleteTransaction,
        filterTransactions, clearFilters, deleteFilteredTransactions
    } = useTransactions('income')

    const [editIncome, setEditIncome] = useState(null)
    const [isEditModalOpen, setIsEditModalOpen] = useState(false)
    const [fieldDescription, setFieldDescription] = useState('')
    const [startDate, setStartDate] = useState('')
    const [endDate, setEndDate] = useState('')
    const [categorySelect, setCategorySelect] = useState('')
    const [minAmount, setMinAmount] = useState('')
    const [maxAmount, setMaxAmount] = useState('')
    const [currentFilters, setCurrentFilters] = useState({})
    const [filterLoading, setFilterLoading] = useState(false)

    const categories = ['Salary', 'Investments', 'Others']

    const handleAddIncome = async (income) => {
        await createTransaction(income)
    }

    const handleDeleteIncome = async (id) => {
        await deleteTransaction(id)
        await loadTransactions()
    }

    const handleEditIncome = (income) => {
        setEditIncome(income)
        setIsEditModalOpen(true)
    }

    const handleUpdateIncome = async (income) => {
        if (!income || !editIncome) return
        await updateTransaction(editIncome.id, income)
        setIsEditModalOpen(false)
        setEditIncome(null)
    }

    const handleCloseModal = () => {
        setIsEditModalOpen(false)
        setEditIncome(null)
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

    const handleDeleteFilteredIncomes = async () => {
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
    }

    if (loading && transactions.length === 0) return <div><Spinner /></div>
    if (error) return <div>Error: {error}</div>

    return (
        <>
            <h2>Incomes Page</h2>
            <IncomeForm onSubmit={handleAddIncome} />
            <IncomeEdit
                income={editIncome}
                isOpen={isEditModalOpen}
                onClose={handleCloseModal}
                onSubmit={handleUpdateIncome}
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
                onDeleteFilteredTransactions={handleDeleteFilteredIncomes}
                isFiltered={isFiltered}
            />
            {filterLoading ? (
                <div><Spinner /></div>
            ) : (
                <IncomeList
                    incomes={transactions}
                    onDelete={handleDeleteIncome}
                    onEdit={handleEditIncome}
                    currentFilters={currentFilters}
                />
            )}
        </>
    )
}