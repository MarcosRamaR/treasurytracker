import { useState, useEffect } from 'react'
import { authService } from '../services/authService'
import '../styles/RegisterLoginStyle.css'

export function RegisterForm({onSubmit,editUser, onSwitchToLogin}) {
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: ''
    })
    
    const [errors, setErrors] = useState({})
    const [isLoading, setIsLoading] = useState(false)

    useEffect(() => {
        if (editUser) {
        setFormData({
            username: editUser.username,
            email: editUser.email,
            password: ''
        })
        }
    }, [editUser])

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

            if(result.token){
                authService.saveData(result.token, {email: result.email, username: result.userName})
                setFormData({username:'',email:'', password: ''})
                if(onSubmit){
                    onSubmit(result)
                }
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
                <form onSubmit={handleSubmit}>
                    <div className="mb-3">
                        <label htmlFor="username" className='label-text'>Username</label>
                        <input
                            type="text"
                            className="form-control"
                            id="username"
                            name="username"
                            value={formData.username}
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
                    {errors.error && (
                        <div className="alert alert-danger">{errors.error}</div>
                    )}
                    <button type="submit" className='register-login-button' disabled={isLoading}>Register</button>
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