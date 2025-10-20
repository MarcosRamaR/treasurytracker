import { ExpenseSummary } from "../components/ExpenseSummary"
import { useExpenses } from "../hooks/useExpenses"

export function SummaryPage () {
    const {expenses, loading, error} = useExpenses()

    if (loading) return <div>Loading summary...</div>
    if (error) return <div>Error: {error}</div>
  return (
    <>
        <h2>Summary Page</h2>
        <ExpenseSummary expenses={expenses}/>
    </>
  )
}
