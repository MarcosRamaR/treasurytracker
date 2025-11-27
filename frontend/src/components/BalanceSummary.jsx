import { useBalance } from "../hooks/useBalance"
import { useExpenses } from "../hooks/useExpenses"
import { useIncomes } from "../hooks/useIncomes"
import { useState } from "react"
import '../styles/GraphsStyle.css'

export function BalanceSummary(){
    const {balance, updateManualBalance} = useBalance()
    const {expenses} = useExpenses()
    const {incomes} = useIncomes()


    const [newBalance, setNewBalance] = useState('')

    const today = new Date()
    const nextDays = new Date();
    const currentYear = today.getFullYear();
    nextDays.setDate(today.getDate() + 30);
    const endYear = new Date(currentYear, 11, 31, 23, 59, 59);


    const futureNextIncomes = incomes.filter(income =>{
        const incomeDate = new Date(income.date)
        return incomeDate > today && incomeDate <= nextDays
    })
    const futureNextExpenses = expenses.filter(expense =>{
        const expenseDate = new Date(expense.date)
        return expenseDate > today && expenseDate <= nextDays
    })
        const futureYearIncomes = incomes.filter(income =>{
        const incomeDate = new Date(income.date)
        return incomeDate > today && incomeDate <= endYear
    })
    const futureYearExpenses = expenses.filter(expense =>{
        const expenseDate = new Date(expense.date)
        return expenseDate > today && expenseDate <= endYear
    })
    const totalFutureIncomes = futureNextIncomes.reduce((sum, income) => sum + income.amount, 0)
    const totalFutureExpenses = futureNextExpenses.reduce((sum, expense) => sum + expense.amount, 0)
    const expectedNextBalance = balance + totalFutureIncomes - totalFutureExpenses

    const totalYearIncomes = futureYearIncomes.reduce((sum, income) => sum + income.amount, 0)
    const totalYearExpenses = futureYearExpenses.reduce((sum, expense) => sum + expense.amount, 0)
    const expectedYearBalance = balance + totalYearIncomes - totalYearExpenses

    const handleBalanceUpdate = async () => {
        if(newBalance === '' || isNaN(newBalance))return
        await updateManualBalance(parseFloat(newBalance))
        setNewBalance('')
    }


    return(
        <>
        <input placeholder="Set new balance amount" value={newBalance} type="number" onChange={e=>setNewBalance(e.target.value)}/>
        <button className="balance-button" onClick={handleBalanceUpdate}>Update</button>
        <div className="balance-summary">
            <div>
                <h3>Current Balance</h3>
                <p> ${balance.toFixed(2)}</p>
            </div>
            <div>
                <h3>30-Day Balance</h3>
                <p> ${expectedNextBalance.toFixed(2)}</p>
            </div>
            <div>
                <h3>Year-End Balance</h3>
                <p> ${expectedYearBalance.toFixed(2)}</p>
            </div>
        </div>
        </>

    )

}