import {useState, useEffect} from 'react'
import { authService } from '../services/authService';
import { LoginForm } from '../components/LoginForm';
import { RegisterForm } from '../components/RegisterForm';

export function HomePage()  {
  const [isLogged, setIsLogged] = useState(false);
  const [showRegister, setShowRegister] = useState(false);

    useEffect(() => {
      const authenticated = authService.isAuthenticated();
      setIsLogged(authenticated);
  }, []);
  const handleLoginSuccess = () => {
    setIsLogged(true);
    setShowRegister(false);
  };

  const handleRegisterSuccess = () => {
    setIsLogged(true);
    setShowRegister(false);
  };

  const handleLogout = () => {
    setIsLogged(false);
    setShowRegister(false);
  };

  const handleSwitchToRegister = () => {
    setShowRegister(true);
  };

  const handleSwitchToLogin = () => {
    setShowRegister(false);
  };
  return (
    <div className="container">
      <div className="row justify-content-center">
        <div className="col-md-6">
          <h1 className="text-center mb-4">Welcome to Treasury Tracker</h1>
          
          {isLogged ? (
            <div className="card">
              <div className="card-body">
                <p>You are now logged in. Use the navigation menu to manage your finances.</p>
                <button onClick={handleLogout} className="btn btn-danger">Logout</button>
              </div>
            </div>
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
    </div>
  )
}
