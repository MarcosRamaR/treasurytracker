import { useState } from "react";
import { authService } from '../services/authService'

export function LoginForm({onSwitchToRegister, onLoginSuccess}){
    //Set form state
    const [formData, setFormData] = useState({
        email: '',
        password: ''
    });
    const [errors, setErrors] = useState({});
    const [isLoading, setIsLoading] = useState(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setErrors({});

        try{
            const result = await authService.login(formData)

            if(result.token){
                authService.saveData(result.token,{email:result.email, username: result.username})
                setFormData({email: '', username: ''})
                if(onLoginSuccess){
                    onLoginSuccess(result)
                }
            }else{
                setErrors(result)
            }
        }catch(error){
            setErrors(error)
        }finally{
            setIsLoading(false)
        }
    };

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

            {errors.error && (
            <div className="alert alert-danger">{errors.error}</div>)}
            
            <button type="submit" disabled={isLoading}>{isLoading ? 'Loggin in...': 'Login'}</button>
            <div className="text-center mt-3">
                <p className="mb-0">
                Don't have an account?{' '}
                    <button type="button" onClick={onSwitchToRegister}>Register</button>
                </p>
            </div>
        </form>
        </div>
    </div>
    )
}