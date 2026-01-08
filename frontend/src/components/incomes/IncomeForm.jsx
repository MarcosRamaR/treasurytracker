import { useState} from "react"
import '../../styles/ExpensesStyle.css'

export function IncomeForm({onSubmit}) {

    const [formData, setFormData] = useState({
        description: '',
        amount: '',
        date: new Date().toISOString().split('T')[0],
        category: 'Others'
    })


  const handleSubmit = (e) => {
    e.preventDefault()
    if (!formData.description.trim() || !formData.amount || !formData.category) return
    onSubmit({
        description: formData.description,
        amount: parseFloat(formData.amount),
        category: formData.category,
        date: formData.date
    })

    setFormData({
        description: '',
        amount: '',
        date: new Date().toISOString().split('T')[0],
        category: 'Others'
    })
    
}
    const categories = ['Salary', 'Investiments', 'Others']

    return (
    <div className="filter-section">
        <h3 className="expense-title">Add Income</h3>
        <form onSubmit={handleSubmit} className="filter-controls">
            <input type="text" 
            className="form-input"
            placeholder="Description" 
            value={formData.description} 
            onChange={(e) => setFormData({...formData, description: e.target.value})} />

            <input type="number" 
            className="form-input"
            placeholder="Amount (€)" 
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
            <button type="submit" className="filter-button">Add</button>
        </form>
    </div>
  )
}