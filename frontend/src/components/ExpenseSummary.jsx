export function ExpenseSummary({expenses}){
    const total = expenses.reduce((sum, expense) => sum + expense.amount, 0)

    const avg = expenses.length > 0 ? (total / expenses.length) : 0

    return(
        <>
            <h3>Expenses Summary</h3>
            <div>
                <p>Total Expenses: ${total.toFixed(2)}</p>
                <p>Average Expense: ${avg.toFixed(2)}</p>
            </div>
        </>
    )

}