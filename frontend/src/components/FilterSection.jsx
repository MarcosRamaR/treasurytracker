import '../styles/ExpensesStyle.css'

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
    isFiltered
}) {

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
        </div>
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