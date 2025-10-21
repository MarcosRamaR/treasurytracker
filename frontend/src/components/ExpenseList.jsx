import '../styles/ExpensesStyle.css';

export function ExpenseList({expenses, onDelete, onEdit}) {

  return (
    <>
        <div>
            <h3 className='expense-title'>Expense List</h3>
            {expenses.length === 0 ? (
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