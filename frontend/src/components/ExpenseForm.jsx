//Form to add or edit an expense

import { useState } from "react"

export function ExpenseForm({onSubmit}) {

    const [formData, setFormData] = useState({
        description: '',
        amount: '',
        date: '',
        category: ''
    })

    const handleSubmit = (e) => {
        e.preventDefault()
        if(!formData) return
        onSubmit({...formData,
            amount: parseFloat(formData.amount)}) //Call parent function
        setFormData({description: '', amount: '', date: '', category: ''}) //Reset form
    }
    const categories = ['Food', 'Transport', 'Entertainment ', 'Others']

  return (
    <div>
        <h3>Add Expense</h3>
        <form onSubmit={handleSubmit}>
            <input type="text" placeholder="Description" value={formData.description} onChange={(e) => setFormData({...formData, description: e.target.value})} />
            <input type="number" placeholder="Amount" value={formData.amount} onChange={(e) => setFormData({...formData, amount: e.target.value})} />
            <input type="date" placeholder="Date" value={formData.date} onChange={(e) => setFormData({...formData, date: e.target.value})} />
            <select>
                value={formData.category} onChange={(e) => setFormData({...formData, category: e.target.value})}
                <option value="">Select Category</option>
                {categories.map(cat => (
                    <option key={cat} value={cat}>{cat}</option>
                ))}
            </select>
            <button type="submit">Add Expense</button>
        </form>
    </div>
  )
}