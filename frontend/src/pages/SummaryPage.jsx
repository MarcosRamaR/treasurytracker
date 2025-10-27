import { ExpenseSummary } from "../components/ExpenseSummary"
import { IncomeSummary } from "../components/IncomeSummary"
import { DashboardCharts } from "../components/DashboardCharts"
import { useExpenses } from "../hooks/useExpenses"
import { useIncomes } from "../hooks/useIncomes"

export function SummaryPage () {
    const {expenses} = useExpenses()
    const {incomes} = useIncomes()

  return (
    <div>
        <h2>Graphs</h2>
        <DashboardCharts expenses={expenses} incomes={incomes} />
        <h2>Expenses Summary</h2>
        <ExpenseSummary expenses={expenses}/>
        <h2>Incomes Summary</h2>
        <IncomeSummary incomes={incomes}/>
    </div>
  )
}
