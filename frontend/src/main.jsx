import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import { TreasuryApp } from './TreasuryApp.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <TreasuryApp/>
  </StrictMode>,
)
