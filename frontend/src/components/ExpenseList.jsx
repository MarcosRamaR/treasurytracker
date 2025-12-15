import '../styles/ExpensesStyle.css';
import { useExport } from '../hooks/useExport';
import { useState } from 'react';

export function ExpenseList({expenses, onDelete, onEdit,currentFilters}) {
    const { exportAllTransactionsOneTypeToCsv,exportFilteredTransactionsToCsv,exportAllTransactionsToCsv,exportAllFilteredTransactionsToCsv, exporting} = useExport();
    const [selectedExportOption, setSelectedExportOption] = useState('All Expenses');
    const exportOptions = ['All Expenses','Filtered Expenses','All Transactions','All Filtered Transactions'];

    const handleExport = async () => {
        if (!selectedExportOption) {
            alert('Please select an export option');
            return;
        }
        try {
            switch(selectedExportOption) {
                case 'All Expenses':
                    await exportAllTransactionsOneTypeToCsv('expense');
                    break;
                case 'Filtered Expenses':
                    await exportFilteredTransactionsToCsv(currentFilters,'expense');
                    break;
                case 'All Transactions':
                    await exportAllTransactionsToCsv();
                    break;
                case 'All Filtered Transactions':
                    await exportAllFilteredTransactionsToCsv(currentFilters);
                    break;
                default:
                    alert('Invalid export option');
            }
        } catch (err) {
            console.error('Export failed:', err);
        }
        console.log('Exported:', selectedExportOption);
        console.log('With filters:', currentFilters);
    };

    return (
    <>
        <div>
            <div>
                <h3 className='expense-title'>Expense List</h3>
                <div className='filter-controls'>
                    <select className="form-input" 
                    onChange={(e) => setSelectedExportOption(e.target.value)}>
                        <option value="" hidden>Export Options</option>
                        {exportOptions.map(option => (
                            <option key={option} value={option}>{option}</option>
                        ))}
                    </select>
                    <button className='filter-button' type = "button" onClick={handleExport}>{exporting ? 'Exporting' : 'Export'}</button>
                </div>
            </div>
            
            {!expenses ? (
                <p>No filter applied.</p>
            ):expenses.length === 0 ? (
                <p>No expenses recorded.</p>
            ) : (
            <table className="expense-table">
                <thead>
                    <tr>
                        <th>Description</th>
                        <th>Amount</th>
                        <th>Date</th>
                        <th>Category</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {expenses.map(expense => (
                        <tr key={expense.id}>
                            <td>{expense.description}</td>
                            <td>{expense.amount} €</td>
                            <td>{expense.date}</td>
                            <td>{expense.category}</td>
                            <td>
                                <button onClick={() => onEdit(expense)}>Edit</button>
                                <button onClick={() => onDelete(expense.id)}>Delete</button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>     
            )}
        </div>
    </>
    )
}