const API_BASE_URL = 'http://localhost:8080/api/auth'

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
        const data = await response.json()
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
        const data = await response.json()
        return data
    }catch (error) {
            console.error('Error during login:', error)
            throw error
        }
    },
    //Save token and user data in local storage
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