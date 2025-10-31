import { authService } from "../services/authService";

export function UserProfile({onLogout}) {
    const user = authService.getUser();

    const handleLogout = () => {
        authService.logout();
        if (onLogout) {
        onLogout();
    }
  };
  return (
    <div>
        <h5>User Profile</h5>
        <div>
            <div>
                <strong>Username: </strong>
                {user?.username || 'N/A'}
            </div>
        </div>
        
        <div>
            <div>
                <strong>Email: </strong>
                {user?.email || 'N/A'}
            </div>
        </div>
        <button onClick={handleLogout}>Logout</button>
    </div>
  );
}