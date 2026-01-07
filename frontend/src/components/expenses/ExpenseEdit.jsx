import { Modal } from '../ModalBasic.jsx';
import { useState, useEffect } from 'react';


export function ExpenseEdit({expense,isOpen,onClose,onSubmit}) {
    const [formData, setFormData] = useState({
        description:'',
        amount:'',
        date:  new Date().toISOString().split('T')[0],
        category: 'Others'
    })

    useEffect(() => {
        if (expense) {
        setFormData({
            description: expense.description,
            amount: expense.amount.toString(),
            category: expense.category,
            date: expense.date
        })
        }
    }, [expense]) //Any time expense changes, update formData

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
        onClose() //Close modal after submit
    }

    const categories = ['Food', 'Transport', 'Entertainment', 'Others'];

    return(
        <Modal isOpen={isOpen} onClose={onClose} title="Edit Expense">
            <form onSubmit={handleSubmit} className="modal-form">
                <div>
                    <label>Description</label>
                    <input type="text" 
                    className="form-input"
                    placeholder="Description" 
                    value={formData.description} 
                    onChange={(e) => setFormData({...formData, description: e.target.value})}/>
                </div>
                <div>
                    <label>Amount</label>
                    <input type="number" 
                    className="form-input"
                    placeholder="Amount (€)" 
                    value={formData.amount} 
                    onChange={(e) => setFormData({...formData, amount: e.target.value})}/>
                </div>
                <div>
                    <label>Date</label>
                    <input type="date" 
                    className="form-input"
                    value={formData.date} 
                    onChange={(e) => setFormData({...formData, date: e.target.value})}/>
                </div>
                <div>
                    <label>Category</label>
                    <select 
                    className="form-input"
                    value={formData.category}
                    onChange={(e) => setFormData({...formData, category: e.target.value})}>
                        {categories.map((cat) => (
                            <option key={cat} value={cat}>{cat}</option>
                        ))}
                    </select>
                </div>
                <div className="modal-buttons">
                    <button type="submit" className="btn btn-primary">Save</button>
                    <button type="button" className="btn btn-secondary" onClick={onClose}>Cancel</button>
                </div>
            </form>
        </Modal>
    )
}