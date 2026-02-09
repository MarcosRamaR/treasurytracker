import '../styles/ExpensesStyle.css'

export function Spinner() {
    return (
        <div className="spinner-container">
            <div className="spinner"></div>
            <div className="spinner-text"> Loading transactions...</div>
        </div>
    )
}