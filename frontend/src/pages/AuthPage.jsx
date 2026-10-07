import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { 
  Flame, 
  Mail, 
  Lock, 
  User, 
  Eye, 
  EyeOff, 
  ArrowRight, 
  AlertCircle, 
  CheckCircle2, 
  Loader2,
  Sparkles
} from 'lucide-react'
import { getApiUrl } from '../api/config'

export default function AuthPage({ initialMode = 'login' }) {
  const [isLogin, setIsLogin] = useState(initialMode === 'login')
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: ''
  })
  const [showPassword, setShowPassword] = useState(false)
  const [loading, setLoading] = useState(false)
  const [errorMessage, setErrorMessage] = useState(null)
  const [successMessage, setSuccessMessage] = useState(null)

  const { login } = useAuth()
  const navigate = useNavigate()

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
    if (errorMessage) setErrorMessage(null)
  }

  const switchMode = (mode) => {
    setIsLogin(mode)
    navigate(mode ? '/login' : '/signup', { replace: true })
    setErrorMessage(null)
    setSuccessMessage(null)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setErrorMessage(null)
    setSuccessMessage(null)

    // Basic Client Validation
    if (!formData.email.trim() || !formData.password.trim()) {
      setErrorMessage('Please fill in all required fields.')
      return
    }

    if (!isLogin && !formData.name.trim()) {
      setErrorMessage('Please enter your full name.')
      return
    }

    if (!isLogin && formData.password.length < 6) {
      setErrorMessage('Password must be at least 6 characters long.')
      return
    }

    setLoading(true)

    try {
      const endpoint = isLogin ? getApiUrl('/auth/login') : getApiUrl('/auth/signup')
      const payload = isLogin
        ? { email: formData.email.trim(), password: formData.password }
        : { name: formData.name.trim(), email: formData.email.trim(), password: formData.password }

      const response = await fetch(endpoint, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
      })

      const data = await response.json().catch(() => null)

      if (!response.ok) {
        let msg = 'Authentication failed. Please try again.'
        if (data && data.message) {
          msg = data.message
        } else if (response.status === 401) {
          msg = 'Invalid email or password.'
        } else if (response.status === 400) {
          msg = data?.error || 'Bad request. Check your input details.'
        }
        throw new Error(msg)
      }

      // Successful auth: data has token, userId, name, email
      setSuccessMessage(isLogin ? 'Welcome back! Redirecting...' : 'Account created! Redirecting...')

      // Short delay for visual confirmation before navigating
      setTimeout(() => {
        login(data)
        navigate('/dashboard', { replace: true })
      }, 400)
    } catch (err) {
      setErrorMessage(err.message || 'Something went wrong. Please check connection.')
    } finally {
      setLoading(false)
    }
  }

  const fillDemoAccount = () => {
    setIsLogin(true)
    setFormData({
      name: '',
      email: 'test@test.com',
      password: 'test1234'
    })
    setErrorMessage(null)
  }

  return (
    <div className="min-h-screen bg-[#0B0B0F] flex items-center justify-center p-4 sm:p-6 relative overflow-hidden selection:bg-[#FF4D4D] selection:text-white">
      {/* Dynamic Ambient Background Glows */}
      <div className="fixed inset-0 pointer-events-none overflow-hidden">
        <div className="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[520px] h-[520px] bg-gradient-to-tr from-[#FF4D4D]/15 to-[#FF7A45]/10 rounded-full blur-[140px]" />
        <div className="absolute -bottom-20 -right-20 w-[380px] h-[380px] bg-[#FF4D4D]/10 rounded-full blur-[120px]" />
        <div className="absolute -top-20 -left-20 w-[340px] h-[340px] bg-[#FF7A45]/10 rounded-full blur-[110px]" />
      </div>

      <div className="relative w-full max-w-md z-10 my-8">
        {/* Brand Header */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-gradient-to-tr from-[#FF4D4D] to-[#FF7A45] p-0.5 shadow-xl shadow-[#FF4D4D]/25 mb-4 transform hover:scale-105 transition-transform duration-300">
            <div className="w-full h-full bg-[#1C1C22] rounded-[14px] flex items-center justify-center">
              <Flame className="w-7 h-7 text-[#FF4D4D] fill-[#FF4D4D]/20 animate-pulse" />
            </div>
          </div>
          <h1 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-white">
            Fit<span className="gradient-text">Track</span>
          </h1>
          <p className="text-[#9CA3AF] text-sm mt-1.5 font-medium">
            Elevate your training & reach peak performance
          </p>
        </div>

        {/* Auth Centered Card Panel */}
        <div className="card-panel-glow p-8 sm:p-10 backdrop-blur-xl border border-white/[0.08]">
          {/* Segmented Mode Switcher */}
          <div className="flex rounded-xl bg-[#121217] p-1 mb-8 border border-white/[0.05]">
            <button
              type="button"
              onClick={() => switchMode(true)}
              className={`flex-1 py-2.5 text-xs sm:text-sm font-semibold rounded-lg transition-all duration-200 cursor-pointer ${
                isLogin
                  ? 'bg-gradient-to-r from-[#FF4D4D] to-[#FF7A45] text-white shadow-md shadow-[#FF4D4D]/30'
                  : 'text-[#9CA3AF] hover:text-white'
              }`}
            >
              Log In
            </button>
            <button
              type="button"
              onClick={() => switchMode(false)}
              className={`flex-1 py-2.5 text-xs sm:text-sm font-semibold rounded-lg transition-all duration-200 cursor-pointer ${
                !isLogin
                  ? 'bg-gradient-to-r from-[#FF4D4D] to-[#FF7A45] text-white shadow-md shadow-[#FF4D4D]/30'
                  : 'text-[#9CA3AF] hover:text-white'
              }`}
            >
              Sign Up
            </button>
          </div>

          {/* Form Header */}
          <div className="mb-6">
            <h2 className="text-xl sm:text-2xl font-bold text-white tracking-tight">
              {isLogin ? 'Welcome back' : 'Create an account'}
            </h2>
            <p className="text-xs sm:text-sm text-[#9CA3AF] mt-1">
              {isLogin
                ? 'Enter your credentials to access your dashboard'
                : 'Start tracking your workouts and fitness progress'}
            </p>
          </div>

          {/* Feedback Messages */}
          {errorMessage && (
            <div className="mb-6 p-3.5 rounded-xl bg-red-500/10 border border-red-500/25 flex items-start gap-3 text-xs sm:text-sm text-red-400 animate-in fade-in duration-200">
              <AlertCircle className="w-5 h-5 shrink-0 mt-0.5 text-red-400" />
              <div className="flex-1 font-medium leading-relaxed">{errorMessage}</div>
            </div>
          )}

          {successMessage && (
            <div className="mb-6 p-3.5 rounded-xl bg-emerald-500/10 border border-emerald-500/25 flex items-center gap-3 text-xs sm:text-sm text-emerald-400 animate-in fade-in duration-200">
              <CheckCircle2 className="w-5 h-5 shrink-0 text-emerald-400" />
              <div className="flex-1 font-medium">{successMessage}</div>
            </div>
          )}

          {/* Auth Form */}
          <form onSubmit={handleSubmit} className="space-y-5">
            {!isLogin && (
              <div>
                <label className="block text-xs font-semibold text-[#9CA3AF] uppercase tracking-wider mb-2">
                  Full Name
                </label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-[#9CA3AF]">
                    <User className="w-4 h-4" />
                  </div>
                  <input
                    type="text"
                    name="name"
                    value={formData.name}
                    onChange={handleInputChange}
                    placeholder="Alex Morgan"
                    required={!isLogin}
                    className="w-full pl-10 pr-4 py-3 bg-[#121217] border border-[#2A2A33] rounded-xl text-white placeholder-[#9CA3AF]/50 text-sm focus:outline-none focus:border-[#FF4D4D] focus:ring-2 focus:ring-[#FF4D4D]/20 transition-all duration-200"
                  />
                </div>
              </div>
            )}

            <div>
              <label className="block text-xs font-semibold text-[#9CA3AF] uppercase tracking-wider mb-2">
                Email Address
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-[#9CA3AF]">
                  <Mail className="w-4 h-4" />
                </div>
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleInputChange}
                  placeholder="athlete@example.com"
                  required
                  className="w-full pl-10 pr-4 py-3 bg-[#121217] border border-[#2A2A33] rounded-xl text-white placeholder-[#9CA3AF]/50 text-sm focus:outline-none focus:border-[#FF4D4D] focus:ring-2 focus:ring-[#FF4D4D]/20 transition-all duration-200"
                />
              </div>
            </div>

            <div>
              <div className="flex items-center justify-between mb-2">
                <label className="block text-xs font-semibold text-[#9CA3AF] uppercase tracking-wider">
                  Password
                </label>
                {isLogin && (
                  <span className="text-[11px] text-[#9CA3AF]/80 hover:text-[#FF7A45] transition-colors cursor-pointer">
                    Forgot password?
                  </span>
                )}
              </div>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-[#9CA3AF]">
                  <Lock className="w-4 h-4" />
                </div>
                <input
                  type={showPassword ? 'text' : 'password'}
                  name="password"
                  value={formData.password}
                  onChange={handleInputChange}
                  placeholder={isLogin ? '••••••••' : 'Min 6 characters'}
                  required
                  className="w-full pl-10 pr-11 py-3 bg-[#121217] border border-[#2A2A33] rounded-xl text-white placeholder-[#9CA3AF]/50 text-sm focus:outline-none focus:border-[#FF4D4D] focus:ring-2 focus:ring-[#FF4D4D]/20 transition-all duration-200"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute inset-y-0 right-0 pr-3.5 flex items-center text-[#9CA3AF] hover:text-white transition-colors cursor-pointer"
                  tabIndex={-1}
                  aria-label="Toggle password visibility"
                >
                  {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>
            </div>

            {/* Gradient Primary Button */}
            <button
              type="submit"
              disabled={loading}
              className="w-full gradient-btn py-3.5 px-6 rounded-xl flex items-center justify-center gap-2 text-sm font-bold tracking-wide mt-2 cursor-pointer disabled:opacity-60 disabled:cursor-not-allowed group"
            >
              {loading ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin" />
                  <span>Processing...</span>
                </>
              ) : (
                <>
                  <span>{isLogin ? 'Log In' : 'Sign Up'}</span>
                  <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
                </>
              )}
            </button>
          </form>

          {/* Toggle Link Between Forms */}
          <div className="mt-7 pt-6 border-t border-white/[0.06] text-center text-xs sm:text-sm text-[#9CA3AF]">
            {isLogin ? (
              <p>
                Don't have an account?{' '}
                <button
                  type="button"
                  onClick={() => switchMode(false)}
                  className="text-white font-semibold hover:text-[#FF7A45] underline decoration-[#FF4D4D]/40 underline-offset-4 transition-colors cursor-pointer"
                >
                  Sign Up
                </button>
              </p>
            ) : (
              <p>
                Already have an account?{' '}
                <button
                  type="button"
                  onClick={() => switchMode(true)}
                  className="text-white font-semibold hover:text-[#FF7A45] underline decoration-[#FF4D4D]/40 underline-offset-4 transition-colors cursor-pointer"
                >
                  Log In
                </button>
              </p>
            )}
          </div>

          {/* Quick Demo Autofill Helper */}
          <div className="mt-5 text-center">
            <button
              type="button"
              onClick={fillDemoAccount}
              className="inline-flex items-center gap-1.5 text-xs text-[#9CA3AF]/80 hover:text-[#FF7A45] transition-colors py-1 px-2.5 rounded-lg hover:bg-white/[0.04] cursor-pointer"
            >
              <Sparkles className="w-3.5 h-3.5 text-[#FF7A45]" />
              <span>Fill test demo account (test@test.com)</span>
            </button>
          </div>
        </div>

        {/* Footer info */}
        <p className="text-center text-xs text-[#9CA3AF]/60 mt-6">
          FitTrack &bull; Powered by Spring Boot & React
        </p>
      </div>
    </div>
  )
}
