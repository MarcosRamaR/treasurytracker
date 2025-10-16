import { useState } from "react"
import { ExpenseForm } from "./components/ExpenseForm.jsx"
import { ExpenseList } from "./components/ExpenseList.jsx"

export const TreasuryApp = () => {
  const [expenses, setExpenses] = useState([])

  const handleAddExpense = (expense) => {
    setExpenses([...expenses, expense])
    }
  const handleDeleteExpense = (id) => {
    setExpenses(expenses.filter(item => item.id !== id))
  }

  return (
    <>
      <div>
          <h1>Treasury Tracker</h1>
          <ExpenseForm onSubmit={handleAddExpense}/>
          <ExpenseList expenses={expenses} onDelete={handleDeleteExpense}/>
      </div>
    </>

  )
}
