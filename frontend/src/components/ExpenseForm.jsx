//Form to add or edit an expense

import { useState, useEffect } from "react"
import '../styles/ExpensesStyle.css';

export function ExpenseForm({onSubmit,editExpense}) {

    const [formData, setFormData] = useState({
        description: '',
        amount: '',
        date: new Date().toISOString().split('T')[0],
        category: 'Others'
    })

    useEffect(() => {
        if (editExpense) {
        setFormData({
            description: editExpense.description,
            amount: editExpense.amount.toString(),
            category: editExpense.category,
            date: editExpense.date
        })
        }
    }, [editExpense])

  const handleSubmit = (e) => {
    e.preventDefault()
    if (!formData.description.trim() || !formData.amount || !formData.category) return
    // Call the onSubmit prop with the form data
    onSubmit({
        description: formData.description,
        amount: parseFloat(formData.amount),
        category: formData.category,
        date: formData.date
    })
    // Clear form only if not editing
    if (!editExpense) {
        setFormData({
            description: '',
            amount: '',
            date: new Date().toISOString().split('T')[0],
            category: 'Others'
        })
    }
}
    const categories = ['Food', 'Transport', 'Entertainment ', 'Others']

  return (
    <div>
        <h3 className="expense-title">{editExpense ? 'Edit Expense' : 'Add Expense'}</h3>
        <form onSubmit={handleSubmit} className="filter-controls">
            <input type="text" 
            className="form-input"
            placeholder="Description" 
            value={formData.description} 
            onChange={(e) => setFormData({...formData, description: e.target.value})} />

            <input type="number" 
            className="form-input"
            placeholder="Amount" 
            value={formData.amount} 
            onChange={(e) => setFormData({...formData, amount: e.target.value})} />

            <input type="date"
            className="form-input" 
            placeholder="Date" 
            value={formData.date} 
            onChange={(e) => setFormData({...formData, date: e.target.value})} />

            <select
                className="form-input"
                value={formData.category} onChange={(e) => setFormData({...formData, category: e.target.value})} >
                <option value="">Select Category</option>
                {categories.map(cat => (
                    <option key={cat} value={cat}>{cat}</option>
                ))}
            </select>
            {/*Button that change on "create mode" or "edit mode"*/}
            <button type="submit">
                {editExpense ? 'Edit' : 'Add'} 
            </button>
            {/*Allow cancel edit mode with a new button*/}
            {editExpense && (
                <button type="button" onClick={() => onSubmit(null)} >Cancel</button>
            )}
        </form>
    </div>
  )
}