import {useState, useEffect} from 'react'
import { authService } from '../services/authService'
import { LoginForm } from '../components/LoginForm'
import { RegisterForm } from '../components/RegisterForm'
import { UserProfile } from '../components/UserProfile'
import '../styles/RegisterLoginStyle.css'

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
      <div className="row justify-content-center">
        <div className="col-md-6">
          {isLogged ? <h1 className="text-center mb-4">Welcome to Treasury Tracker, <b>{user.userName}</b></h1> : <h1 className="text-center mb-4">Welcome to Treasury Tracker</h1>}
          {isLogged ? (
            <>
              <UserProfile onLogout={handleLogout}/>
            </>):(
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
    </div>
  )
}
