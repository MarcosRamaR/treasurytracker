import '../styles/ExpensesStyle.css'
import { useState } from 'react'
import { ConfirmDeleteModal } from './ModalConfirmDelete.jsx'

export function FilterSection({
    fieldDescription,
    setFieldDescription,
    startDate,
    setStartDate,
    endDate,
    setEndDate,
    categorySelect,
    setCategorySelect,
    minAmount,
    setMinAmount,
    maxAmount,
    setMaxAmount,
    categories,
    onFilter,
    onClearFilters,
    onDeleteFilteredTransactions,
    isFiltered
}) {
    const [showConfirmModal, setShowConfirmModal] = useState(false)

    const getCurrentFilters = () => {
        return {
            description: fieldDescription,
            category: categorySelect,
            startDate: startDate,
            endDate: endDate,
            minAmount: minAmount,
            maxAmount: maxAmount
        }
    }

    const handleDeleteClick = () => {
        const hasActiveFilters = fieldDescription || categorySelect || startDate || endDate || minAmount || maxAmount
        if (!hasActiveFilters) {
            alert('Please apply at least one filter before deleting. To delete all expenses, please remove them individually.')
            return
        }
        setShowConfirmModal(true)
    }

    const handleConfirmDelete = () => {
        const filters = getCurrentFilters()
        onDeleteFilteredTransactions(filters)
        setShowConfirmModal(false)
    }

    return (
    <div className="filter-section">
        <h3>Filters</h3>
        <div className="filter-controls">
            <label>
                Description:
                <input 
                type="text"
                value ={fieldDescription}
                onChange={(e) => setFieldDescription(e.target.value)}
                />
            </label>
            <label>
                Start Date:
                <input 
                type="date" 
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
                />
            </label>
            <label>
                End Date:
                <input 
                type="date" 
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
                />
            </label>
            <label>
                Lower Amount (€):
                <input 
                type="number" 
                value={minAmount}
                onChange={(e) => setMinAmount(e.target.value)}
                />
            </label>
            <label>
                High Amount (€):
                <input 
                type="number" 
                value={maxAmount}
                onChange={(e) => setMaxAmount(e.target.value)}
                />
            </label>
            <label>
                Category:
                <select
                className="form-input"
                value={categorySelect} 
                onChange={(e) => setCategorySelect(e.target.value)}
                >
                <option value="">Select Category</option>
                {categories.map(cat => (
                    <option key={cat} value={cat}>{cat}</option>
                ))}
                </select>
            </label>

            <button className="filter-button" onClick={onFilter}>
                Filter
            </button>
            <button className="filter-button" onClick={onClearFilters}>
                Reset Filters
            </button>
            <button className="filter-button" onClick={handleDeleteClick}>
                Delete
            </button>
        </div>
        <ConfirmDeleteModal
                isOpen={showConfirmModal}
                onClose={() => setShowConfirmModal(false)}
                onConfirm={handleConfirmDelete}
                itemType="filtered expenses"
            />
        {isFiltered && (
        <div className="filter-info">
            {categorySelect && `Category: ${categorySelect}`}
            {categorySelect && (startDate || endDate) && ' | '}
            {startDate && endDate && `Date range: ${startDate} to ${endDate}`}
        </div>
        )}
    </div>
    )
}