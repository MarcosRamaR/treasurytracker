import {NavLink} from 'react-router-dom';
import '../styles/NavBarStyle.css';

export const NavBar = () => {
  return (
    <nav className="navbar navbar-expand-lg bg-body-tertiary sticky-top">
        <div className="container-fluid" >
            <NavLink to='/' className="navbar-brand">Treasury Tracker</NavLink>
            <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span className="navbar-toggler-icon"></span>
            </button>
            <div className="collapse navbar-collapse" id="navbarNav">
                <ul className="navbar-nav me-auto mb-2 mb-lg-0">
                    <li className="nav-item">
                        <NavLink to='/summary' className={({ isActive }) => isActive ? "nav-link active" : "nav-link"}>
                        Summary</NavLink>
                    </li>
                    <li className="nav-item">
                        <NavLink to='/expenses' className={({ isActive }) => isActive ? "nav-link active" : "nav-link"}>
                        Expenses</NavLink>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
  )
}
