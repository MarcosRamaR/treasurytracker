
export function LoginForm({onSwitchToRegister, onLoginSuccess}){
    return(
    <div >
        <div >
        <h5 >Login</h5>
        <form>
            <div>
            <label htmlFor="loginEmail" className="form-label">Email</label>
            <input type="email" className="form-control" id="loginEmail" name="email" required
            />
            </div>
            <div>
            <label htmlFor="loginPassword" className="form-label">Password</label>
            <input type="password" className="form-control" id="loginPassword" name="password" required
            />
            </div>
            <button type="submit">Login</button>
        </form>
        </div>
    </div>
    )
}