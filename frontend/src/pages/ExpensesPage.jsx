import { ExpenseForm } from "../components/ExpenseForm";
import { ExpenseList } from "../components/ExpenseList";
import { useExpenses } from "../hooks/useExpenses";
import { useState } from "react";

export function ExpensesPage() {
    const {expenses, loading, error, createExpense, updateExpense, deleteExpense} = useExpenses()
    const [editExpense, setEditExpense] = useState(null)

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

    if (loading) return <div>Loading expenses...</div>
    if (error) return <div>Error: {error}</div>
    //For edit button (ExpenseList) call handleEditExpense, passing the expense to edit and set it in state (editExpense)
    // after that pass it to ExpenseForm as prop (editingExpense) and handleUpdateExpense as onSubmit 
    return (
    <>
        <h2>Expenses Page</h2>
        <ExpenseForm onSubmit={editExpense ? handleUpdateExpense : handleAddExpense} editExpense={editExpense}/>
        <ExpenseList expenses={expenses} onDelete={handleDeleteExpense} onEdit={handleEditExpense}/> 
    </>
    
  )
}
