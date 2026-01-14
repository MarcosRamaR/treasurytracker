const API_BASE_URL = import.meta.env.VITE_API_AUTH_BASE
    ? `${import.meta.env.VITE_API_AUTH_BASE}/api/auth`
    : 'http://localhost:8081/api/auth';

export const authService = {
    async register(userData) {
        try{
            //Call to backend API
        const response = await fetch(`${API_BASE_URL}/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData) //Object to JSON
        })
        const contentType = response.headers.get('content-type') //Verify response have content
        let data

        if(contentType && contentType.includes('application/json')) {
            data = await response.json()
        } else {
            //If response void or not JSON, create custom error message
            data = {
                error: response.status === 403 
                    ? 'User already exists' 
                    : `Registration failed (${response.status})`
            }
        }
        console.log('Register data:', data)
        return data
        
    }catch (error) {
            console.error('Error during registration:', error)
            throw error
        }
    },
    async login(credentials) {
        try{
        const response = await fetch(`${API_BASE_URL}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(credentials) 
        })
        console.log('Login response status:', response.status)
        const token = response.headers.get('Authorization') //Token now in header
        if (!response.ok) {
            let errorMessage = 'Invalid email or password'
            
            try {
                const errorData = await response.json()
                if (errorData.message || errorData.error) {
                    errorMessage = errorData.message || errorData.error
                    console.log('Login error message from server:', errorMessage)
                    console.log('Full error data from server:', errorData)
                }
            } catch {
                if (response.status === 401 || response.status === 403) {
                    errorMessage = 'Invalid email or password'
                    console.log(errorMessage)
                    console.log('Login failed with status:', response)
                } else {
                    errorMessage = `Login failed (${response.status})`
                }
            }
            
            throw new Error(errorMessage)
        }
        const data = await response.json()
        console.log('Login data:', data)

        if(!token){
            throw new Error('No token received')
        }
        this.saveData(token,  {
        email: data.email,
        username: data.userName,
        id: data.id
      }) //Save token and user data

        return { token, ...data}
    }catch (error) {
            console.error('Error during login:', error)
            throw error
        }
    },
    //Save token WITH "Bearer" and user data in local storage
    saveData(token, userData){
        localStorage.setItem('token', token)
        localStorage.setItem('user', JSON.stringify(userData))
    },
    logout(){
        localStorage.removeItem('token')
        localStorage.removeItem('user')
    }, //Check if user is logged in
    isAuthenticated(){
        return !!localStorage.getItem('token')
    },
    getToken(){
        return localStorage.getItem('token')
    },
    getUser(){
        const user = localStorage.getItem('user')
        return user ? JSON.parse(user) : null
    }
}