import { Routes, Route } from "react-router-dom"
import { NavBar } from "./components/NavBar.jsx"
import { SummaryPage } from './pages/SummaryPage'
import { ExpensesPage } from './pages/ExpensesPage'
import { HomePage } from './pages/HomePage'
import {IncomesPage} from './pages/IncomesPage'
import './styles/TreasuryAppStyle.css';
import './styles/NavBarStyle.css';

export const TreasuryApp = () => {
  return (
      <div className="fixed-top">
        <NavBar/>
        <div className="container mt-2">
          <Routes>
            <Route path="/" element={<HomePage/>}/>
            <Route path="/summary" element={<SummaryPage/>}/>
            <Route path="/expenses" element={<ExpensesPage/>}/>
            <Route path="/incomes" element={<IncomesPage/>}/>
          </Routes>
        </div>
      </div>
  )
}
