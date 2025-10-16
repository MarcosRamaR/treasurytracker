//Form to add or edit an expense

export function ExpenseForm() {

  return (
    <div>
        <h3>Add Expense</h3>
        <form>
            <input type="text" placeholder="Description" />
            <input type="number" placeholder="Amount" />
            <input type="date" placeholder="Date" />
            <input type="text" placeholder="Category" />
            <button type="submit">Add Expense</button>
        </form>
    </div>
  )
}