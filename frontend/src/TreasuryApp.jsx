import { useState } from "react"
import { ExpenseForm } from "./components/ExpenseForm.jsx"
import { ExpenseList } from "./components/ExpenseList.jsx"
import { ExpenseSummary } from "./components/ExpenseSummary.jsx"

export const TreasuryApp = () => {
  const [expenses, setExpenses] = useState([])
  const [editExpense, setEditExpense] = useState(null)

  const handleAddExpense = (expense) => {
    setExpenses([...expenses, expense])
    }
  const handleDeleteExpense = (id) => {
    setExpenses(expenses.filter(item => item.id !== id))
  }

  const handleEditExpense = (expense) => {
    setEditExpense(expense)
  }

const handleUpdateExpense = (expense) => {
  // Get the id of the expense being edited
  if (!expense) {
    setEditExpense(null)
    return
  }
  const idExpense = editExpense.id
  const updatedExpenses = expenses.map(item => {
    if (item.id === idExpense) {
      //Create a new object merging old and new data
      const updatedData = {
        ...item,          //The old data
        ...expense     //The new data overrides the old one
      }
      return updatedData
    }
    return item
  })
  //Update the state with the new array
  setExpenses(updatedExpenses)
  setEditExpense(null)
}

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
