import { ExpenseForm } from "../components/ExpenseForm";
import { ExpenseList } from "../components/ExpenseList";
import { useExpenses } from "../hooks/useExpenses";
import { useState } from "react";
import '../styles/ExpensesStyle.css';

export function ExpensesPage() {
    const {expenses, loading, error,isFiltered, 
        createExpense, updateExpense, deleteExpense,filterExpenses,clearFilters
    } = useExpenses()
    const [editExpense, setEditExpense] = useState(null)
    const [startDate, setStartDate] = useState('')
    const [endDate, setEndDate] = useState('')
    const [categorySelect, setCategorySelect] = useState('')
    const [minAmount, setMinAmount] = useState(0)
    const [maxAmount, setMaxAmount] = useState(0)
    const categories = ['Food', 'Transport', 'Entertainment ', 'Others']

    const handleAddExpense = async (expense) => {
    await createExpense(expense)
    }
    const handleDeleteExpense = async(id) => {
    await deleteExpense(id)
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
        await filterExpenses(categorySelect,startDate, endDate, minAmount, maxAmount)
    }
    const handleClearFilters = () => {
        clearFilters()
        setCategorySelect('')
        setStartDate('')
        setEndDate('')
    }


    if (loading) return <div>Loading expenses...</div>
    if (error) return <div>Error: {error}</div>
    //For edit button (ExpenseList) call handleEditExpense, passing the expense to edit and set it in state (editExpense)
    // after that pass it to ExpenseForm as prop (editingExpense) and handleUpdateExpense as onSubmit 
    return (
    <>
        <h2>Expenses Page</h2>
        <ExpenseForm onSubmit={editExpense ? handleUpdateExpense : handleAddExpense} editExpense={editExpense}/>
        <div className="filter-section">
            <h3>Filters</h3>
            <div className="filter-controls">
                <label>
                    Start Date:
                    <input 
                        type="date" 
                        value={startDate}
                        onChange={(e) => setStartDate(e.target.value)}
                    />
                </label>
                <label>
                    End Date:
                    <input 
                        type="date" 
                        value={endDate}
                        onChange={(e) => setEndDate(e.target.value)}
                    />
                </label>
                                <label>
                    Lower Amount:
                    <input 
                        type="number" 
                        value={minAmount}
                        onChange={(e) => setMinAmount(e.target.value)}
                    />
                </label>
                <label>
                    Hight Amount:
                    <input 
                        type="number" 
                        value={maxAmount}
                        onChange={(e) => setMaxAmount(e.target.value)}
                    />
                </label>
                <label>
                    Category:
                <select
                    className="form-input"
                    value={categorySelect} onChange={(e) => setCategorySelect(e.target.value)} >
                    <option value="">Select Category</option>
                    {categories.map(cat => (
                        <option key={cat} value={cat}>{cat}</option>
                    ))}
                </select>
                </label>

                <button className="filter-button" onClick={handleFilters}>
                    Filter
                </button>
                <button className="filter-button" onClick={handleClearFilters}>
                    Reset Filters
                </button>
            </div>
        </div>
        {isFiltered && (
            <div className="filter-info">
                {categorySelect && `Category: ${categorySelect}`}
                {categorySelect && (startDate || endDate) && ' | '}
                {startDate && endDate && `Date range: ${startDate} to ${endDate}`}
            </div>
        )}
        <ExpenseList expenses={expenses} onDelete={handleDeleteExpense} onEdit={handleEditExpense}/> 
    </>
    
  )
}
