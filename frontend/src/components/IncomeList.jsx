import '../styles/ExpensesStyle.css';

export function IncomeList({incomes, onDelete, onEdit}) {

  return (
    <>
        <div>
            <h3 className='expense-title'>Income List</h3>
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