import { DashboardCharts } from "../components/charts/DashboardCharts"
import { useTransactions } from "../hooks/useTransactions"
import { BalanceSummary } from "../components/BalanceSummary"

export function SummaryPage () {
    const {transactions: expenses} = useTransactions('expense')
    const {transactions: incomes} = useTransactions('income')


  return (
    <div>
        <h2>Graphs</h2>
        <DashboardCharts expenses={expenses} incomes={incomes} />
        <h2>Balance Summary</h2>
          <BalanceSummary/>
    </div>
  )
}
