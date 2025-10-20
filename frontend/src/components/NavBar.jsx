import {NavLink} from 'react-router-dom';

export const NavBar = () => {
  return (
    <nav className="navbar navbar-expand-lg bg-body-tertiary">
        <div class="container-fluid">
            <NavLink to='/' className="navbar-brand">Treasury Tracker</NavLink>
            <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span className="navbar-toggler-icon"></span>
            </button>
            <div className="collapse navbar-collapse" id="navbarNav">
                <ul className="navbar-nav">
                    <li className="nav-item">
                    <NavLink to='/summary' className={({ isActive }) => isActive ? "nav-link active" : "nav-link"}>Summary</NavLink>
                    </li>
                    <li className="nav-item">
                    <NavLink to='/expenses' className={({ isActive }) => isActive ? "nav-link active" : "nav-link"}>Expenses</NavLink>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

  )
}
