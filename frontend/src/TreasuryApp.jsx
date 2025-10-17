import { useState } from "react"
import { ExpenseForm } from "./components/ExpenseForm.jsx"
import { ExpenseList } from "./components/ExpenseList.jsx"
import { ExpenseSummary } from "./components/ExpenseSummary.jsx"
import { useExpenses } from "./hooks/useExpenses.js"

export const TreasuryApp = () => {
  const {
    expenses,
    loading,
    error,
    createExpense,
    updateExpense,
    deleteExpense
  } = useExpenses()

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
      <div>
          <h1>Treasury Tracker</h1>
          <ExpenseSummary expenses={expenses}/>
          <ExpenseForm onSubmit={editExpense ? handleUpdateExpense : handleAddExpense} editExpense={editExpense}/>
          <ExpenseList expenses={expenses} onDelete={handleDeleteExpense} onEdit={handleEditExpense}/> 
      </div>
    </>

  )
}
