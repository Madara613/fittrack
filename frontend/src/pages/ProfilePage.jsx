import React, { useState, useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { getApiUrl } from '../api/config'
import { 
  Flame, 
  User, 
  Mail, 
  Ruler, 
  Scale, 
  Target, 
  Calendar, 
  Save, 
  CheckCircle2, 
  AlertCircle, 
  ArrowLeft, 
  LogOut, 
  Loader2, 
  Sparkles,
  TrendingUp,
  Activity
} from 'lucide-react'

export default function ProfilePage() {
  const { user, token, logout } = useAuth()
  const navigate = useNavigate()

  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [errorMessage, setErrorMessage] = useState(null)
  const [successMessage, setSuccessMessage] = useState(null)

  const [profileData, setProfileData] = useState({
    name: user?.name || '',
    email: user?.email || '',
    height: '',
    weight: '',
    goalWeight: '',
    weeklyTarget: ''
  })

  // Fetch current user profile
  useEffect(() => {
    const fetchProfile = async () => {
      if (!token) return
      setLoading(true)
      setErrorMessage(null)

      try {
        const res = await fetch(getApiUrl('/profile'), {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        })

        if (!res.ok) {
          if (res.status === 401) {
            logout()
            navigate('/login')
            return
          }
          throw new Error(`Failed to load profile (HTTP ${res.status})`)
        }

        const data = await res.json()
        setProfileData({
          name: data.name || user?.name || 'Athlete',
          email: data.email || user?.email || '',
          height: data.height != null ? data.height : '',
          weight: data.weight != null ? data.weight : '',
          goalWeight: data.goalWeight != null ? data.goalWeight : '',
          weeklyTarget: data.weeklyTarget != null ? data.weeklyTarget : ''
        })
      } catch (err) {
        setErrorMessage(err.message || 'Error loading profile')
      } finally {
        setLoading(false)
      }
    }

    fetchProfile()
  }, [token])

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setProfileData(prev => ({ ...prev, [name]: value }))
    if (successMessage) setSuccessMessage(null)
    if (errorMessage) setErrorMessage(null)
  }

  const handleSave = async (e) => {
    e.preventDefault()
    setSaving(true)
    setErrorMessage(null)
    setSuccessMessage(null)

    try {
      const payload = {
        height: profileData.height !== '' ? parseFloat(profileData.height) : null,
        weight: profileData.weight !== '' ? parseFloat(profileData.weight) : null,
        goalWeight: profileData.goalWeight !== '' ? parseFloat(profileData.goalWeight) : null,
        weeklyTarget: profileData.weeklyTarget !== '' ? parseInt(profileData.weeklyTarget, 10) : null
      }

      const res = await fetch(getApiUrl('/profile'), {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      })

      if (!res.ok) {
        if (res.status === 401) {
          logout()
          navigate('/login')
          return
        }
        const errorData = await res.json().catch(() => null)
        throw new Error(errorData?.message || `Failed to update profile (HTTP ${res.status})`)
      }

      const updated = await res.json()
      setProfileData(prev => ({
        ...prev,
        height: updated.height != null ? updated.height : '',
        weight: updated.weight != null ? updated.weight : '',
        goalWeight: updated.goalWeight != null ? updated.goalWeight : '',
        weeklyTarget: updated.weeklyTarget != null ? updated.weeklyTarget : ''
      }))

      setSuccessMessage('Profile updated successfully!')
      setTimeout(() => setSuccessMessage(null), 4000)
    } catch (err) {
      setErrorMessage(err.message || 'Error saving profile')
    } finally {
      setSaving(false)
    }
  }

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  // Calculated Insights
  const heightM = profileData.height ? parseFloat(profileData.height) / 100 : null
  const weightKg = profileData.weight ? parseFloat(profileData.weight) : null
  const goalKg = profileData.goalWeight ? parseFloat(profileData.goalWeight) : null

  let bmi = null
  let bmiCategory = null
  if (heightM && weightKg && heightM > 0) {
    bmi = (weightKg / (heightM * heightM)).toFixed(1)
    if (bmi < 18.5) bmiCategory = { label: 'Underweight', color: 'text-amber-400 bg-amber-400/10' }
    else if (bmi < 25) bmiCategory = { label: 'Healthy Weight', color: 'text-emerald-400 bg-emerald-400/10' }
    else if (bmi < 30) bmiCategory = { label: 'Overweight', color: 'text-amber-400 bg-amber-400/10' }
    else bmiCategory = { label: 'Obese', color: 'text-rose-400 bg-rose-400/10' }
  }

  const weightDiff = (weightKg && goalKg) ? (goalKg - weightKg).toFixed(1) : null

  return (
    <div className="min-h-screen bg-[#0B0B0F] text-slate-100 selection:bg-[#FF4D4D] selection:text-white pb-16">
      {/* Background Ambient Glows */}
      <div className="fixed inset-0 pointer-events-none overflow-hidden">
        <div className="absolute top-0 right-1/4 w-[600px] h-[600px] bg-[#FF4D4D]/10 rounded-full blur-[160px]" />
        <div className="absolute top-1/2 left-0 w-[500px] h-[500px] bg-[#FF7A45]/8 rounded-full blur-[140px]" />
      </div>

      <div className="relative max-w-4xl mx-auto px-4 sm:px-6 z-10">
        {/* Navigation Bar */}
        <header className="flex items-center justify-between py-6 border-b border-white/[0.06] mb-8 sm:mb-10">
          <div className="flex items-center gap-3">
            <Link to="/dashboard" className="flex items-center gap-3 group">
              <div className="w-11 h-11 rounded-2xl bg-gradient-to-tr from-[#FF4D4D] to-[#FF7A45] p-0.5 shadow-lg shadow-[#FF4D4D]/25 group-hover:scale-105 transition-transform">
                <div className="w-full h-full bg-[#1C1C22] rounded-[14px] flex items-center justify-center">
                  <Flame className="w-5 h-5 text-[#FF4D4D] fill-[#FF4D4D]/20" />
                </div>
              </div>
              <div>
                <span className="text-xl font-extrabold tracking-tight text-white">
                  Fit<span className="gradient-text">Track</span>
                </span>
                <span className="hidden sm:inline-block ml-2 text-[10px] font-semibold uppercase tracking-wider px-2 py-0.5 rounded-full bg-white/[0.05] text-[#9CA3AF] border border-white/[0.08]">
                  Athlete Profile
                </span>
              </div>
            </Link>
          </div>

          <div className="flex items-center gap-3">
            <Link
              to="/dashboard"
              className="flex items-center gap-1.5 px-3.5 py-2 rounded-xl text-xs font-semibold text-[#9CA3AF] hover:text-white bg-[#1C1C22] hover:bg-[#25252D] border border-white/[0.08] transition-all cursor-pointer"
            >
              <ArrowLeft className="w-3.5 h-3.5" />
              <span>Back to Dashboard</span>
            </Link>

            <button
              onClick={handleLogout}
              className="flex items-center gap-1.5 px-3.5 py-2 rounded-xl text-xs font-semibold text-[#9CA3AF] hover:text-white bg-[#1C1C22] hover:bg-[#25252D] border border-white/[0.08] transition-all cursor-pointer"
            >
              <LogOut className="w-3.5 h-3.5" />
              <span>Log Out</span>
            </button>
          </div>
        </header>

        {/* User Identity Header Card */}
        <div className="card-panel-glow p-7 sm:p-9 mb-8 relative overflow-hidden">
          <div className="flex flex-col sm:flex-row sm:items-center gap-5 sm:gap-6">
            <div className="w-20 h-20 rounded-2xl bg-gradient-to-tr from-[#FF4D4D] to-[#FF7A45] p-1 shadow-xl shadow-[#FF4D4D]/25 shrink-0">
              <div className="w-full h-full bg-[#1C1C22] rounded-[14px] flex items-center justify-center font-extrabold text-2xl text-transparent bg-clip-text bg-gradient-to-r from-[#FF4D4D] to-[#FF7A45]">
                {profileData.name ? profileData.name.charAt(0).toUpperCase() : 'U'}
              </div>
            </div>

            <div className="flex-1 min-w-0">
              <div className="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full bg-[#FF4D4D]/10 border border-[#FF4D4D]/25 text-[#FF4D4D] text-[11px] font-bold mb-2">
                <Sparkles className="w-3 h-3" />
                <span>Verified Athlete</span>
              </div>
              <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight truncate">
                {profileData.name || 'Athlete'}
              </h1>
              <p className="text-[#9CA3AF] text-sm flex items-center gap-2 mt-1 truncate">
                <Mail className="w-4 h-4 text-[#9CA3AF]/80 shrink-0" />
                <span>{profileData.email || 'user@example.com'}</span>
              </p>
            </div>
          </div>
        </div>

        {/* Feedback Alerts */}
        {errorMessage && (
          <div className="mb-6 p-4 rounded-xl bg-red-500/10 border border-red-500/25 flex items-center gap-3 text-sm text-red-400">
            <AlertCircle className="w-5 h-5 shrink-0" />
            <div className="flex-1 font-medium">{errorMessage}</div>
          </div>
        )}

        {successMessage && (
          <div className="mb-6 p-4 rounded-xl bg-emerald-500/10 border border-emerald-500/25 flex items-center gap-3 text-sm text-emerald-400 animate-in fade-in duration-200">
            <CheckCircle2 className="w-5 h-5 shrink-0" />
            <div className="flex-1 font-medium">{successMessage}</div>
          </div>
        )}

        {/* Main Content Layout */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 sm:gap-8">
          {/* Editable Form (2 Cols) */}
          <div className="lg:col-span-2">
            <div className="card-panel p-6 sm:p-8 border border-white/[0.08]">
              <div className="mb-6">
                <h2 className="text-lg sm:text-xl font-bold text-white tracking-tight">Fitness Metrics & Goals</h2>
                <p className="text-xs sm:text-sm text-[#9CA3AF] mt-1">
                  Keep your physical measurements and targets up to date for accurate fitness calculations.
                </p>
              </div>

              {loading ? (
                <div className="text-center py-12 text-[#9CA3AF] text-sm">
                  <Loader2 className="w-6 h-6 animate-spin mx-auto mb-3 text-[#FF4D4D]" />
                  <span>Loading your athlete profile...</span>
                </div>
              ) : (
                <form onSubmit={handleSave} className="space-y-5">
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
                    {/* Height */}
                    <div>
                      <label className="block text-xs font-semibold text-[#9CA3AF] uppercase tracking-wider mb-2">
                        Height (cm)
                      </label>
                      <div className="relative">
                        <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-[#9CA3AF]">
                          <Ruler className="w-4 h-4" />
                        </div>
                        <input
                          type="number"
                          step="0.5"
                          min="50"
                          max="260"
                          name="height"
                          value={profileData.height}
                          onChange={handleInputChange}
                          placeholder="e.g. 178"
                          className="w-full pl-10 pr-4 py-3 bg-[#121217] border border-[#2A2A33] rounded-xl text-white placeholder-[#9CA3AF]/40 text-sm focus:outline-none focus:border-[#FF4D4D] focus:ring-2 focus:ring-[#FF4D4D]/20 transition-all duration-200"
                        />
                      </div>
                      <span className="text-[11px] text-[#9CA3AF]/60 mt-1 block">Ex: 178 cm</span>
                    </div>

                    {/* Current Weight */}
                    <div>
                      <label className="block text-xs font-semibold text-[#9CA3AF] uppercase tracking-wider mb-2">
                        Current Weight (kg)
                      </label>
                      <div className="relative">
                        <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-[#9CA3AF]">
                          <Scale className="w-4 h-4" />
                        </div>
                        <input
                          type="number"
                          step="0.1"
                          min="20"
                          max="300"
                          name="weight"
                          value={profileData.weight}
                          onChange={handleInputChange}
                          placeholder="e.g. 75.5"
                          className="w-full pl-10 pr-4 py-3 bg-[#121217] border border-[#2A2A33] rounded-xl text-white placeholder-[#9CA3AF]/40 text-sm focus:outline-none focus:border-[#FF4D4D] focus:ring-2 focus:ring-[#FF4D4D]/20 transition-all duration-200"
                        />
                      </div>
                      <span className="text-[11px] text-[#9CA3AF]/60 mt-1 block">Ex: 75.5 kg</span>
                    </div>

                    {/* Goal Weight */}
                    <div>
                      <label className="block text-xs font-semibold text-[#9CA3AF] uppercase tracking-wider mb-2">
                        Goal Weight (kg)
                      </label>
                      <div className="relative">
                        <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-[#9CA3AF]">
                          <Target className="w-4 h-4" />
                        </div>
                        <input
                          type="number"
                          step="0.1"
                          min="20"
                          max="300"
                          name="goalWeight"
                          value={profileData.goalWeight}
                          onChange={handleInputChange}
                          placeholder="e.g. 70.0"
                          className="w-full pl-10 pr-4 py-3 bg-[#121217] border border-[#2A2A33] rounded-xl text-white placeholder-[#9CA3AF]/40 text-sm focus:outline-none focus:border-[#FF4D4D] focus:ring-2 focus:ring-[#FF4D4D]/20 transition-all duration-200"
                        />
                      </div>
                      <span className="text-[11px] text-[#9CA3AF]/60 mt-1 block">Target body mass</span>
                    </div>

                    {/* Weekly Target */}
                    <div>
                      <label className="block text-xs font-semibold text-[#9CA3AF] uppercase tracking-wider mb-2">
                        Weekly Target (Sessions)
                      </label>
                      <div className="relative">
                        <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-[#9CA3AF]">
                          <Calendar className="w-4 h-4" />
                        </div>
                        <input
                          type="number"
                          step="1"
                          min="1"
                          max="14"
                          name="weeklyTarget"
                          value={profileData.weeklyTarget}
                          onChange={handleInputChange}
                          placeholder="e.g. 4"
                          className="w-full pl-10 pr-4 py-3 bg-[#121217] border border-[#2A2A33] rounded-xl text-white placeholder-[#9CA3AF]/40 text-sm focus:outline-none focus:border-[#FF4D4D] focus:ring-2 focus:ring-[#FF4D4D]/20 transition-all duration-200"
                        />
                      </div>
                      <span className="text-[11px] text-[#9CA3AF]/60 mt-1 block">Workouts per week</span>
                    </div>
                  </div>

                  <div className="pt-4 border-t border-white/[0.06] flex items-center justify-end">
                    <button
                      type="submit"
                      disabled={saving}
                      className="gradient-btn py-3 px-6 rounded-xl flex items-center justify-center gap-2 text-sm font-bold tracking-wide cursor-pointer disabled:opacity-60 disabled:cursor-not-allowed group w-full sm:w-auto"
                    >
                      {saving ? (
                        <>
                          <Loader2 className="w-4 h-4 animate-spin" />
                          <span>Saving Changes...</span>
                        </>
                      ) : (
                        <>
                          <Save className="w-4 h-4" />
                          <span>Save Profile</span>
                        </>
                      )}
                    </button>
                  </div>
                </form>
              )}
            </div>
          </div>

          {/* Fitness Insights & Calculations Sidebar */}
          <div className="space-y-5">
            {/* BMI Card */}
            <div className="card-panel p-6 border border-white/[0.08]">
              <div className="flex items-center justify-between mb-3">
                <span className="text-xs font-semibold text-[#9CA3AF] uppercase tracking-wider">Body Mass Index</span>
                <Activity className="w-4 h-4 text-[#FF4D4D]" />
              </div>

              {bmi ? (
                <div>
                  <div className="flex items-baseline gap-2">
                    <div className="text-3xl font-extrabold text-white">{bmi}</div>
                    <span className="text-xs text-[#9CA3AF]">kg/m²</span>
                  </div>
                  {bmiCategory && (
                    <div className="mt-2">
                      <span className={`inline-block px-2.5 py-0.5 rounded-full text-xs font-semibold ${bmiCategory.color}`}>
                        {bmiCategory.label}
                      </span>
                    </div>
                  )}
                </div>
              ) : (
                <div className="text-xs text-[#9CA3AF]/70 italic py-2">
                  Enter both height and weight to calculate your BMI automatically.
                </div>
              )}
            </div>

            {/* Goal Weight Delta */}
            <div className="card-panel p-6 border border-white/[0.08]">
              <div className="flex items-center justify-between mb-3">
                <span className="text-xs font-semibold text-[#9CA3AF] uppercase tracking-wider">Target Delta</span>
                <TrendingUp className="w-4 h-4 text-[#FF7A45]" />
              </div>

              {weightDiff !== null ? (
                <div>
                  <div className="text-2xl font-extrabold text-white">
                    {weightDiff > 0 ? `+${weightDiff}` : weightDiff} <span className="text-xs font-normal text-[#9CA3AF]">kg</span>
                  </div>
                  <p className="text-xs text-[#9CA3AF] mt-1.5 leading-relaxed">
                    {weightDiff == 0 
                      ? 'You have reached your target weight goal!'
                      : weightDiff < 0 
                      ? `${Math.abs(weightDiff)} kg to reduce to reach target.`
                      : `${weightDiff} kg to gain to reach target.`}
                  </p>
                </div>
              ) : (
                <div className="text-xs text-[#9CA3AF]/70 italic py-2">
                  Enter current weight and goal weight to see your progress delta.
                </div>
              )}
            </div>

            {/* Weekly Target Badge */}
            <div className="card-panel p-6 border border-white/[0.08]">
              <div className="flex items-center justify-between mb-3">
                <span className="text-xs font-semibold text-[#9CA3AF] uppercase tracking-wider">Weekly Commitment</span>
                <Calendar className="w-4 h-4 text-[#FF4D4D]" />
              </div>

              <div className="text-2xl font-extrabold text-white">
                {profileData.weeklyTarget ? `${profileData.weeklyTarget} Sessions` : '--'}
              </div>
              <p className="text-xs text-[#9CA3AF] mt-1">
                Planned workouts per week
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
