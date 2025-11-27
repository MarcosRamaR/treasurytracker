import { DashboardCharts } from "../components/DashboardCharts"
import { useExpenses } from "../hooks/useExpenses"
import { useIncomes } from "../hooks/useIncomes"
import { BalanceSummary } from "../components/BalanceSummary"

export function SummaryPage () {
    const {expenses} = useExpenses()
    const {incomes} = useIncomes()


  return (
    <div>
        <h2>Graphs</h2>
        <DashboardCharts expenses={expenses} incomes={incomes} />
        <h2>Balance Summary</h2>
          <BalanceSummary/>
    </div>
  )
}
