import { Routes, Route } from "react-router-dom"
import { NavBar } from "./components/NavBar.jsx"
import { SummaryPage } from './pages/SummaryPage'
import { ExpensesPage } from './pages/ExpensesPage'


export const TreasuryApp = () => {
  return (
      <div className="container">
        <NavBar/>
        <Routes>
          <Route path="/" element={<SummaryPage/>}/>
          <Route path="/summary" element={<SummaryPage/>}/>
          <Route path="/expenses" element={<ExpensesPage/>}/>
        </Routes>
      </div>
  )
}
