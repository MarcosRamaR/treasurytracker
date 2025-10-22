import { IncomeForm } from "../components/IncomeForm";
import { IncomeList } from "../components/IncomeList";
import { FilterSection } from "../components/FilterSection";
import { useIncomes } from "../hooks/useIncomes";
import { useState } from "react";
import '../styles/ExpensesStyle.css';

export function IncomesPage() {
    const {incomes, loading, error,isFiltered, 
        createIncome, updateIncome, deleteIncome,filterIncomes,clearFilters
    } = useIncomes()
    const [editIncome, setEditIncome] = useState(null)
    const [startDate, setStartDate] = useState('')
    const [endDate, setEndDate] = useState('')
    const [categorySelect, setCategorySelect] = useState('')
    const [minAmount, setMinAmount] = useState('')
    const [maxAmount, setMaxAmount] = useState('')
    const categories = ['Food', 'Transport', 'Entertainment', 'Others']

    const handleAddIncome = async (income) => {
    await createIncome(income)
    }
    const handleDeleteIncome = async(id) => {
    await deleteIncome(id)
    }

    const handleEditIncome = (income) => {
    setEditIncome(income)
    }
    const handleUpdateIncome = async (income) => {
        if (!income) {
        setEditIncome(null)
        return
        }
        await updateIncome(editIncome.id, income)
        setEditIncome(null)
    }
    const handleFilters = async () => {
        const filters = {
            category: categorySelect,
            startDate: startDate,
            endDate: endDate,
            minAmount: minAmount ? parseFloat(minAmount) : null,
            maxAmount: maxAmount ? parseFloat(maxAmount) : null
        }
        await filterIncomes(filters)
    }
    const handleClearFilters = () => {
        clearFilters()
        setCategorySelect('')
        setStartDate('')
        setEndDate('')
    }


    if (loading) return <div>Loading incomes...</div>
    if (error) return <div>Error: {error}</div>
    return (
    <>
      <h2>Incomes Page</h2>
      <IncomeForm onSubmit={editIncome ? handleUpdateIncome : handleAddIncome} editIncome={editIncome}/>
      <FilterSection
        startDate={startDate}
        setStartDate={setStartDate}
        endDate={endDate}
        setEndDate={setEndDate}
        categorySelect={categorySelect}
        setCategorySelect={setCategorySelect}
        minAmount={minAmount}
        setMinAmount={setMinAmount}
        maxAmount={maxAmount}
        setMaxAmount={setMaxAmount}
        categories={categories}
        onFilter={handleFilters}
        onClearFilters={handleClearFilters}
        isFiltered={isFiltered}
        />
      <IncomeList incomes={incomes} onDelete={handleDeleteIncome} onEdit={handleEditIncome}/> 
    </>
    
  )
}
