import {useState, useEffect} from 'react'
import { authService } from '../services/authService'
import { LoginForm } from '../components/auth/LoginForm'
import { RegisterForm } from '../components/auth/RegisterForm'
import { UserProfile } from '../components/auth/UserProfile'
import '../styles/RegisterLoginStyle.css'
import { AccountBalance, PieChart, AttachMoney } from '@mui/icons-material';

export function HomePage()  {
  const [isLogged, setIsLogged] = useState(false)
  const [showRegister, setShowRegister] = useState(false)
  const user = JSON.parse(localStorage.getItem('user'))

    useEffect(() => {
      const authenticated = authService.isAuthenticated()
      setIsLogged(authenticated)
      console.log('isAuthenticated:', authenticated)
  }, [])
  
  const handleLoginSuccess = () => {
    setIsLogged(true)
    setShowRegister(false)
  }

  const handleRegisterSuccess = () => {
    setIsLogged(false)
    setShowRegister(false)
  }

  const handleLogout = () => {
    setIsLogged(false)
    setShowRegister(false)
  }

  const handleSwitchToRegister = () => {
    setShowRegister(true)
  }

  const handleSwitchToLogin = () => {
    setShowRegister(false)
  }


    return (
    <div className="container-register-login">
      <div className="app-header">
        <div className="app-logo">
          <AttachMoney sx={{ fontSize: 48, color: '#059669' }} />
          <h1>Treasury Tracker</h1>
        </div>
      </div>
      
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className="welcome-section">
            {isLogged ? (
              <h1 className="welcome-title">Welcome to<span className="brand-highlight">Treasury Tracker</span>, <b className="username-highlight">{user.username}</b></h1>
            ) : (
              <h1 className="welcome-title">
                Control your Treasury <span className="animated-text">Intelligently</span>
              </h1>
            )}
            {!isLogged && (
              <p className="welcome-subtitle">
                Visualize expenses, income, and forecasts with interactive charts
              </p>
            )}
          </div>
          
          {isLogged ? (
            <>
              <div className="login-success-animation">
                <div className="success-checkmark">
                  <div className="check-icon"></div>
                </div>
                <p className="login-success-message">Session started successfully</p>
              </div>
              <UserProfile onLogout={handleLogout}/>
            </>
          ) : (
            <>
              {showRegister ? (
                <RegisterForm 
                  onSwitchToLogin={handleSwitchToLogin}
                  onSubmit={handleRegisterSuccess}
                />
              ) : (
                <LoginForm 
                  onSwitchToRegister={handleSwitchToRegister}
                  onLoginSuccess={handleLoginSuccess}
                />
              )}
              
            </>
          )}
        </div>
      </div>
      
      {!isLogged && (
        <div className="app-footer">
          <p className="footer-text">
            Join <span className="footer-highlight">now</span> to control your finances in the best way
          </p>
        </div>
      )}
    </div>
  )
}