import { ExpenseForm } from "../components/ExpenseForm"
import { ExpenseList } from "../components/ExpenseList"
import { FilterSection } from "../components/FilterSection"
import { useExpenses } from "../hooks/useExpenses"
import { useState } from "react"
import '../styles/ExpensesStyle.css'

export function ExpensesPage() {
    const {expenses, loading, error,isFiltered, 
        loadExpenses,createExpense, updateExpense, deleteExpense,filterExpenses,clearFilters
    } = useExpenses()
    const [editExpense, setEditExpense] = useState(null)
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
    await createExpense(expense)
    }
    const handleDeleteExpense = async(id) => {
    await deleteExpense(id)
    await loadExpenses()
    }

    const handleEditExpense = (expense) => {
    setEditExpense(expense)
    }
    const handleUpdateExpense = async (expense) => {
    // Get the id of the expense being edited
        if (!expense) {
        setEditExpense(null)
        return
        }
        await updateExpense(editExpense.id, expense)
        setEditExpense(null)
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
        await filterExpenses(filters)
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
        setFilterLoading(false)
    }


    if (loading && expenses.length === 0) return <div>Loading expenses...</div>
    if (error && expenses.length === 0) return <div>Error: {error}</div>
    //For edit button (ExpenseList) call handleEditExpense, passing the expense to edit and set it in state (editExpense)
    // after that pass it to ExpenseForm as prop (editingExpense) and handleUpdateExpense as onSubmit 
    return (
    <>
    <h2>Expenses Page</h2>
    <ExpenseForm onSubmit={editExpense ? handleUpdateExpense : handleAddExpense} editExpense={editExpense}/>
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
        isFiltered={isFiltered}
        />

        {filterLoading ? (
            <div style={{ padding: '20px', textAlign: 'center' }}>Applying filters...</div>) : (
            <ExpenseList 
                expenses={expenses} 
                onDelete={handleDeleteExpense} 
                onEdit={handleEditExpense} 
                currentFilters={currentFilters}/>)} 
    </>
    
  )
}
