import {NavLink, useNavigate} from 'react-router-dom'
import '../styles/NavBarStyle.css'
import {authService} from '../services/authService'
import {useState, useEffect} from 'react'

export const NavBar = () => {
    // Check if user is authenticated and user data
    const [isLogged, setIsLogged] = useState(false)
    const [user, setUser] = useState(null)
    const navigate = useNavigate()

    useEffect(() => {
    const checkAuth = () => {
        setIsLogged(authService.isAuthenticated())
        setUser(authService.getUser())
    }
    //Verify with load
    checkAuth()

    //Look for changes on storage
    const handleStorageChange = () => {
        checkAuth()
    }
    window.addEventListener('storage', handleStorageChange)
    //Periodic check
    const interval = setInterval(checkAuth, 1000)
    return () => {
        window.removeEventListener('storage', handleStorageChange)
        clearInterval(interval)
    }
    }, [])

    //Functon to allow logout
    const handleLogout = () => {
        authService.logout()
        setIsLogged(false)
        setUser(null)
        navigate('/')
    }

    return (
    <nav className="navbar navbar-expand-lg bg-body-tertiary sticky-top">
        <div className="container-fluid" >
            <NavLink to='/' className="navbar-brand">Treasury Tracker</NavLink>
            {/*Show name if logged*/}
            {isLogged && (
                <span className="navbar-text me-3"> Welcome, {user.username}!</span>
            )}
            <button className="navbar-dark navbar-toggler custom-color-navbar" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span className="navbar-toggler-icon"></span>
            </button>

            <div className="collapse navbar-collapse" id="navbarNav">
                <ul className="navbar-nav me-auto mb-2 mb-lg-0">
                    {isLogged ? (
                        <>
                    <li className="nav-item">
                        <NavLink to='/summary' className={({ isActive }) => isActive ? "nav-link active" : "nav-link"}>
                        Summary</NavLink>
                    </li>
                    <li className="nav-item">
                        <NavLink to='/expenses' className={({ isActive }) => isActive ? "nav-link active" : "nav-link"}>
                        Expenses</NavLink>
                    </li>
                    <li className="nav-item">
                        <NavLink to='/incomes' className={({ isActive }) => isActive ? "nav-link active" : "nav-link"}>
                        Incomes</NavLink>
                    </li>
                    <li className="nav-item">
                    <button 
                        onClick={handleLogout} className="nav-link btn btn-link" >
                        Logout
                    </button>
                    </li>
                    </>
                    ) : (<li className="nav-item">
                        <NavLink to='/' className="nav-link" >Home</NavLink>
                    </li>)}
                    
                </ul>
            </div>
        </div>
    </nav>
  )
}
