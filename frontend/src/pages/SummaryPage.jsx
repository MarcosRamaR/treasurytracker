import { ExpenseSummary } from "../components/ExpenseSummary"
import { IncomeSummary } from "../components/IncomeSummary"
import { useExpenses } from "../hooks/useExpenses"
import { useIncomes } from "../hooks/useIncomes"

export function SummaryPage () {
    const {expenses} = useExpenses()
    const {incomes} = useIncomes()

  return (
    <div>
        <h2>Expenses Summary</h2>
        <ExpenseSummary expenses={expenses}/>
        <h2>Incomes Summary</h2>
        <IncomeSummary incomes={incomes}/>
    </div>
  )
}
