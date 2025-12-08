const API_BASE_URL = 'http://localhost:8081/api/auth'

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
        
        const token = response.headers.get('Authorization') //Token now in header
        const data = await response.json()

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