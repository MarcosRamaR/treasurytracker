import { useState } from "react"
import { ExpenseForm } from "./components/ExpenseForm.jsx"

export const TreasuryApp = () => {
  const [expenses, setExpenses] = useState([])

  const handleAddExpense = (expense) => {
    setExpenses([...expenses, expense])
    }
  

  return (
    <>
      <div>
          <h1>Treasury Tracker</h1>
          <ExpenseForm onSubmit={handleAddExpense}/>
      </div>
    </>

  )
}
