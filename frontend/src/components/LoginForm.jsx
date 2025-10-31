import { useState } from "react";

export function LoginForm({onSwitchToRegister, onLoginSuccess}){
    //Set form state
    const [formData, setFormData] = useState({
        email: '',
        password: ''
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        console.log('Datos del login:', formData);
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
            <input type="password" className="form-control" id="loginPassword" name="password"  value={formData.password} onChange={handleChange} required
            />
            </div>
            <button type="submit">Login</button>
        </form>
        </div>
    </div>
    )
}