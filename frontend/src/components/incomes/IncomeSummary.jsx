export function IncomeSummary({incomes}){
    const total = incomes.reduce((sum, income) => sum + income.amount, 0)
    const avg = incomes.length > 0 ? (total / incomes.length) : 0

    return(
        <>
            <div>
                <p>Total Incomes: ${total.toFixed(2)}</p>
                <p>Average Income: ${avg.toFixed(2)}</p>
            </div>
        </>
    )
}