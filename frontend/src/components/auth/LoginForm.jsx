import { useState } from "react"
import { authService } from '../../services/authService'
import '../../styles/RegisterLoginStyle.css'

export function LoginForm({onSwitchToRegister, onLoginSuccess}){
    //Set form state
    const [formData, setFormData] = useState({
        email: '',
        password: ''
    })
    const [errorMessage, setErrorMessage] = useState()
    const [isLoading, setIsLoading] = useState(false)

    const handleChange = (e) => {
        const { name, value } = e.target
        setFormData(prev => ({
            ...prev,
            [name]: value
        }))
        if (errorMessage) {
            setErrorMessage('')
        }
    }

    const handleSubmit = async (e) => {
        e.preventDefault()
        setIsLoading(true)
        setErrorMessage('')

        try{
            //Send form data to try login
            const result = await authService.login(formData)
            setFormData({email: '', password: ''})
            if (onLoginSuccess) {
            onLoginSuccess(result)
            }
        }catch(error){
            setErrorMessage(error.message || 'Error during login')
            console.error('Login error:', error)
        }finally{
            setIsLoading(false)
        }
    }

    return(
    <div >
        <div >
        <h5 >Login</h5>
        <form onSubmit={handleSubmit}>
            <div>
            <label htmlFor="loginEmail" className="form-label">Email</label>
            <input type="email" className="form-control" id="loginEmail" name="email" value={formData.email} onChange={handleChange} required/>
            </div>
            <div>
            <label htmlFor="loginPassword" className="form-label">Password</label>
            <input type="password" className="form-control" id="loginPassword" name="password"  value={formData.password} onChange={handleChange} required/>
            </div>
            {errorMessage && (
            <div className="alert alert-danger">{errorMessage}</div>)}

            <button className="register-login-button" type="submit" disabled={isLoading}>{isLoading ? 'Loggin in...': 'Login'}</button>
            <div className="text-center mt-3">
                <p className="mb-0">
                Don't have an account?{' '}
                    <button className="register-login-button"  type="button" onClick={onSwitchToRegister}>Register</button>
                </p>
            </div>
        </form>
        </div>
    </div>
    )
}