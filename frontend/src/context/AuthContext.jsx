import React, { createContext, useContext, useState, useEffect, useCallback } from 'react'
import { isTokenValid, decodeJwt } from '../utils/jwt'

const AuthContext = createContext(null)

export const AuthProvider = ({ children }) => {
  // Helper to read valid token from storage
  const getValidStoredToken = () => {
    try {
      const storedToken = localStorage.getItem('fittrack_token')
      if (storedToken && isTokenValid(storedToken)) {
        return storedToken
      }
      // If there's an invalid or expired token, clean it up
      if (storedToken) {
        localStorage.removeItem('fittrack_token')
        localStorage.removeItem('fittrack_user')
      }
    } catch {
      // Ignore storage access errors
    }
    return null
  }

  // Helper to read user matching valid token from storage
  const getStoredUser = () => {
    try {
      const storedToken = localStorage.getItem('fittrack_token')
      if (!storedToken || !isTokenValid(storedToken)) {
        return null
      }
      const saved = localStorage.getItem('fittrack_user')
      return saved ? JSON.parse(saved) : null
    } catch {
      return null
    }
  }

  const [token, setToken] = useState(getValidStoredToken)
  const [user, setUser] = useState(getStoredUser)

  const logout = useCallback(() => {
    setToken(null)
    setUser(null)
    try {
      localStorage.removeItem('fittrack_token')
      localStorage.removeItem('fittrack_user')
    } catch {
      // Ignore storage errors
    }
  }, [])

  const login = useCallback((authData) => {
    if (!authData || !authData.token || !isTokenValid(authData.token)) {
      return false
    }

    setToken(authData.token)

    // Decode token fallback if user info missing in authData
    const payload = decodeJwt(authData.token)
    const userData = {
      userId: authData.userId || payload?.sub,
      name: authData.name || payload?.name || '',
      email: authData.email || payload?.sub || ''
    }

    setUser(userData)
    try {
      localStorage.setItem('fittrack_token', authData.token)
      localStorage.setItem('fittrack_user', JSON.stringify(userData))
    } catch {
      // Ignore storage errors
    }
    return true
  }, [])

  // Sync token validity periodically and across tabs
  useEffect(() => {
    const checkAuthStatus = () => {
      const storedToken = localStorage.getItem('fittrack_token')
      if (storedToken) {
        if (!isTokenValid(storedToken)) {
          logout()
        } else if (storedToken !== token) {
          setToken(storedToken)
          try {
            const saved = localStorage.getItem('fittrack_user')
            setUser(saved ? JSON.parse(saved) : null)
          } catch {
            setUser(null)
          }
        }
      } else if (token) {
        logout()
      }
    }

    // Verify token validity every 15 seconds to handle natural expiry
    const timer = setInterval(checkAuthStatus, 15000)

    const handleStorageEvent = (event) => {
      if (event.key === 'fittrack_token' || event.key === 'fittrack_user') {
        checkAuthStatus()
      }
    }

    window.addEventListener('storage', handleStorageEvent)

    return () => {
      clearInterval(timer)
      window.removeEventListener('storage', handleStorageEvent)
    }
  }, [token, logout])

  // Boolean indicator of whether user has a valid unexpired JWT
  const isAuthenticated = Boolean(token && isTokenValid(token))

  return (
    <AuthContext.Provider
      value={{
        token,
        user,
        login,
        logout,
        isAuthenticated,
        isTokenValid: () => Boolean(token && isTokenValid(token))
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}
