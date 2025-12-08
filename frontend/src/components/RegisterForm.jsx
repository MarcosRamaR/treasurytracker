import { useState, useEffect } from 'react'
import { authService } from '../services/authService'
import '../styles/RegisterLoginStyle.css'

export function RegisterForm({onSubmit, onSwitchToLogin}) {
    const [formData, setFormData] = useState({
        userName: '',
        email: '',
        password: ''
    })
    
    const [errors, setErrors] = useState({})
    const [isLoading, setIsLoading] = useState(false)

    useEffect(() => {
    }, [])

    const handleChange = (e) => {
        const { name, value } = e.target
        setFormData(prev => ({
            ...prev,
            [name]: value
        }))
    }

    const handleSubmit = async (e) => {
        e.preventDefault()
        setIsLoading(true)
        setErrors({})

        try{
            const result = await authService.register(formData)
            console.log('Resultado del registro:', result)
            if(result.id && result.email && result.userName){
                setFormData({userName:'',email:'', password: ''})
                setErrors({success: 'Account created. Redirecting to login'})

                setTimeout(() => {
                if(onSubmit){
                    onSubmit(result)
                }
                },2500)

            }else{
                setErrors(result)
            }
        }catch(error){
            setErrors(error)
        }finally{
            setIsLoading(false)
        }
    }

    return (
        <div className='register-form'>
            <div>
                <h5 className='title-h5'>Create Account</h5>
                <form onSubmit={handleSubmit} >
                    <div className="mb-3">
                        <label htmlFor="username" className='label-text'>Username</label>
                        <input
                            type="text"
                            className="form-control"
                            id="userName"
                            name="userName"
                            value={formData.userName}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    
                    <div className="mb-3">
                        <label htmlFor="email" className='label-text'>Email</label>
                        <input
                            type="email"
                            className="form-control"
                            id="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    
                    <div className="mb-3">
                        <label htmlFor="password" className='label-text'>Password</label>
                        <input
                            type="password"
                            className="form-control"
                            id="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    {errors.success && (
                        <div className="alert alert-success">{errors.success}</div>)}
                    {errors.error && (
                        <div className="alert alert-danger">{errors.error}</div>
                    )}
                    <button type="submit" className='register-login-button' disabled={isLoading}>{isLoading ? 'Registering...' : 'Register'}</button>
                </form>
                
                <div className="text-center mt-3">
                    <p className="mb-0">
                        Already have an account?{' '}
                        <button  type="button"  className="register-login-button" onClick={onSwitchToLogin}>Login here</button>
                    </p>
                </div>
            </div>
        </div>
    )
}