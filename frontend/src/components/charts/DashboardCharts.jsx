import { MonthlyChart } from './MonthlyChart';
import { ExpenseCategoryChart } from './ExpensesCategoryChart';
import { IncomeCategoryChart } from './IncomeCategoryExpenses';
import { MonthlyExpenses } from './MonthlyExpenses';
import '../../styles/GraphsStyle.css'

export function DashboardCharts({ expenses, incomes }) {
    return (
    <div className="dashboard-grid">  
        <div className="chart-card">
            <MonthlyChart expenses={expenses} incomes={incomes}/>
        </div>

        <div className="chart-card">
            <MonthlyExpenses expenses={expenses}/>
        </div>

        <div className="chart-card">
            <ExpenseCategoryChart expenses={expenses} />
        </div>

        <div className="chart-card">
            <IncomeCategoryChart incomes={incomes}/>
        </div>

    </div>
    );
}