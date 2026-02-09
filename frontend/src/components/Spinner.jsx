import '../styles/ExpensesStyle.css'

export function Spinner() {
    return (
        <div className="spinner-container">
            <div className="spinner"></div>
            <div> Loading transactions...</div>
        </div>
    )
}