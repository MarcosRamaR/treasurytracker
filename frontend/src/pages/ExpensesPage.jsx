import { ExpenseForm } from "../components/ExpenseForm";
import { ExpenseList } from "../components/ExpenseList";
import { useExpenses } from "../hooks/useExpenses";
import { useState } from "react";

export function ExpensesPage() {
    const {expenses, loading, error,isFiltered, createExpense, updateExpense, deleteExpense, filterExpensesByDateRange} = useExpenses()
    const [editExpense, setEditExpense] = useState(null)
    const [startDate, setStartDate] = useState(new Date().toISOString().split('T')[0])
    const [endDate, setEndDate] = useState(new Date().toISOString().split('T')[0])

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
    const handleFilterByDateRange = async () => {
        await filterExpensesByDateRange(startDate, endDate)
    }


    if (loading) return <div>Loading expenses...</div>
    if (error) return <div>Error: {error}</div>
    //For edit button (ExpenseList) call handleEditExpense, passing the expense to edit and set it in state (editExpense)
    // after that pass it to ExpenseForm as prop (editingExpense) and handleUpdateExpense as onSubmit 
    return (
    <>
        <h2>Expenses Page</h2>
        <ExpenseForm onSubmit={editExpense ? handleUpdateExpense : handleAddExpense} editExpense={editExpense}/>
        {/* Filtro por fechas */}
<div style={{ margin: '20px 0', padding: '10px', border: '1px solid #ccc', borderRadius: '5px' }}>
    <h3>Filter by Date Range</h3>
    <div style={{ display: 'flex', gap: '10px', alignItems: 'center', flexWrap: 'wrap' }}>
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
        <button onClick={handleFilterByDateRange}>
            Filter
        </button>
    </div>
</div>

{/* Información del filtro aplicado */}
{isFiltered && (
    <div style={{ marginBottom: '10px', color: '#666' }}>
        Showing expenses from {startDate} to {endDate} 
    </div>
)}
        <ExpenseList expenses={expenses} onDelete={handleDeleteExpense} onEdit={handleEditExpense}/> 
    </>
    
  )
}
