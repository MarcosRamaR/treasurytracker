//Form to add or edit an expense

import { useState } from "react"

export function ExpenseForm({onSubmit}) {

    const [description, setDescription] = useState('')
    const [amount, setAmount] = useState('')
    const [date, setDate] = useState('')
    const [category, setCategory] = useState('')

    const handleSubmit = (e) => {
        e.preventDefault()
        if(!description || !amount || !date || !category) return
        onSubmit({description, amount: parseFloat(amount), date, category}) //Call parent function
        //Clear form
        setDescription('')
        setAmount('')
        setDate('')
        setCategory('')
    }

  return (
    <div>
        <h3>Add Expense</h3>
        <form onSubmit={handleSubmit}>
            <input type="text" placeholder="Description" value={description} onChange={(e) => setDescription(e.target.value)} />
            <input type="number" placeholder="Amount" value={amount} onChange={(e) => setAmount(e.target.value)} />
            <input type="date" placeholder="Date" value={date} onChange={(e) => setDate(e.target.value)} />
            <input type="text" placeholder="Category" value={category} onChange={(e) => setCategory(e.target.value)} />
            <button type="submit">Add Expense</button>
        </form>
    </div>
  )
}