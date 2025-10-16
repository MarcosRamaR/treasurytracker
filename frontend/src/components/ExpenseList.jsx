export function ExpenseList({expenses}) {


  return (
    <>
        <div>
            <h3>Expense List</h3>
            {expenses.length === 0 ? (
                <p>No expenses recorded.</p>
            ) : (
            <div>
                {expenses.map(expense => (
                    <div key={expense.id}>
                        <p>{expense.description} - ${expense.amount} on {expense.date} [{expense.category}]</p>
                    </div>
                ))}
            </div>    
            )}
        </div>
    </>
  )
}