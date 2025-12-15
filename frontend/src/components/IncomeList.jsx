import '../styles/ExpensesStyle.css'
import { useExport } from '../hooks/useExport';
import { useState } from 'react';

export function IncomeList({incomes, onDelete, onEdit,currentFilters}) {
        const { exportAllTransactionsOneTypeToCsv,exportFilteredTransactionsToCsv,exportAllTransactionsToCsv,exportAllFilteredTransactionsToCsv, exporting} = useExport();
        const [selectedExportOption, setSelectedExportOption] = useState('All Expenses');
        const exportOptions = ['All Incomes','Filtered Incomes','All Transactions','All Filtered Transactions'];

        const handleExport = async () => {
            if (!selectedExportOption) {
                alert('Please select an export option');
                return;
            }
            try{
                switch(selectedExportOption) {
                    case 'All Incomes':
                        await exportAllTransactionsOneTypeToCsv('income');
                        break;
                    case 'Filtered Incomes':
                        await exportFilteredTransactionsToCsv(currentFilters,'income');
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
            }catch(err){
                console.error('Export failed:', err);
            }
        }
  return (
    <>
        <div>
            <div>
                <h3 className='expense-title'>Income List</h3>
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
            {!incomes ? (
                <p>No filter applied.</p>
            ):incomes.length === 0 ? (
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
                    {incomes.map(incomes => (
                        <tr key={incomes.id}>
                            <td>{incomes.description}</td>
                            <td>{incomes.amount} €</td>
                            <td>{incomes.date}</td>
                            <td>{incomes.category}</td>
                            <td>
                                <button onClick={() => onEdit(incomes)}>Edit</button>
                                <button onClick={() => onDelete(incomes.id)}>Delete</button>
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