import {NavLink} from 'react-router-dom';
import '../styles/NavBarStyle.css';
import {authService} from '../services/authService';

export const NavBar = () => {
    // Check if user is authenticated and user data
    const isLogged = authService.isAuthenticated();
    const user = authService.getUser();

    //Functon to allow logout
    const handleLogout = () => {
        authService.logout();
        window.location.href = '/'; // Redirect to login page after logout
    }

    return (
    <nav className="navbar navbar-expand-lg bg-body-tertiary sticky-top">
        <div className="container-fluid" >
            <NavLink to='/' className="navbar-brand">Treasury Tracker</NavLink>
            {/*Show name if logged*/}
            {isLogged && (
                <span className="navbar-text me-3"> Welcome, {user.username}!</span>
            )}
            <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span className="navbar-toggler-icon"></span>
            </button>

            <div className="collapse navbar-collapse" id="navbarNav">
                <ul className="navbar-nav me-auto mb-2 mb-lg-0">
                    {!isLogged ? (
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
