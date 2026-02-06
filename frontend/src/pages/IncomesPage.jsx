import { IncomeForm } from "../components/incomes/IncomeForm"
import { IncomeList } from "../components/incomes/IncomeList"
import { FilterSection } from "../components/FilterSection"
import { useIncomes } from "../hooks/useIncomes"
import { useState } from "react"
import { IncomeEdit } from "../components/incomes/IncomeEdit"
import { Spinner } from "../components/Spinner"
import '../styles/ExpensesStyle.css'

export function IncomesPage() {
    const {incomes, loading, error,isFiltered, 
        loadIncomes,createIncome, updateIncome, deleteIncome,filterIncomes,clearFilters, deleteFilteredIncomes
    } = useIncomes()
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

    const categories = ['Salary', 'Investiments', 'Others']

    const handleAddIncome = async (income) => {
    await createIncome(income)
    }
    const handleDeleteIncome = async(id) => {
    await deleteIncome(id)
    await loadIncomes()
    }

    const handleEditIncome = (income) => {
    setEditIncome(income)
    setIsEditModalOpen(true)
    }
    const handleUpdateIncome = async (income) => {
        if (!income || !editIncome) return
        
        await updateIncome(editIncome.id, income)
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
            minAmount: minAmount ? parseFloat(minAmount) : null,
            maxAmount: maxAmount ? parseFloat(maxAmount) : null
        }
        setCurrentFilters(filters)
        setFilterLoading(true)
        try{
        await filterIncomes(filters)
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
            minAmount: minAmount ? parseFloat(minAmount) : null,
            maxAmount: maxAmount ? parseFloat(maxAmount) : null
        }
        setCurrentFilters(filters)
        setFilterLoading(true)
        try{
        await deleteFilteredIncomes(filters)
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
        setMinAmount(0)
        setMaxAmount(0)
    }


    if (loading && incomes.length === 0) return <div><div><Spinner /></div><div>Loading incomes...</div></div>
    if (error) return <div>Error: {error}</div>
    return (
    <>
        <h2>Incomes Page</h2>
        <IncomeForm onSubmit={handleAddIncome}/>
        <IncomeEdit
        income={editIncome} //Pass the income
        isOpen={isEditModalOpen} //Control modal visibility
        onClose={handleCloseModal}
        onSubmit={handleUpdateIncome}/>
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
            <div style={{ padding: '20px', textAlign: 'center' }}>Applying filters...</div>) : (
            <IncomeList incomes={incomes} onDelete={handleDeleteIncome} onEdit={handleEditIncome} currentFilters={currentFilters}/>)}
    </>
    
  )
}
