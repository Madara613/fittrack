import React, { useState, useEffect } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { 
  Flame, 
  Dumbbell, 
  Activity, 
  TrendingUp, 
  Calendar, 
  Clock, 
  LogOut, 
  Plus, 
  CheckCircle2, 
  ShieldCheck, 
  User, 
  Trash2,
  AlertCircle,
  Sparkles
} from 'lucide-react'
import { getApiUrl } from '../api/config'

export default function DashboardPage() {
  const { user, token, logout } = useAuth()
  const navigate = useNavigate()

  const [workouts, setWorkouts] = useState([])
  const [loadingWorkouts, setLoadingWorkouts] = useState(true)
  const [workoutError, setWorkoutError] = useState(null)
  const [showAddModal, setShowAddModal] = useState(false)
  const [submittingWorkout, setSubmittingWorkout] = useState(false)
  const [newWorkout, setNewWorkout] = useState({
    type: 'Chest & Triceps',
    sets: 4,
    reps: 10,
    durationMinutes: 45,
    calories: 320
  })

  // Fetch logged-in user workouts using stored JWT
  const fetchWorkouts = async () => {
    if (!token) return
    setLoadingWorkouts(true)
    setWorkoutError(null)

    try {
      const res = await fetch(getApiUrl('/workouts'), {
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
        throw new Error(`Failed to load workouts (HTTP ${res.status})`)
      }

      const data = await res.json()
      setWorkouts(Array.isArray(data) ? data : [])
    } catch (err) {
      setWorkoutError(err.message || 'Error loading workouts')
    } finally {
      setLoadingWorkouts(false)
    }
  }

  useEffect(() => {
    fetchWorkouts()
  }, [token])

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const handleCreateWorkout = async (e) => {
    e.preventDefault()
    setSubmittingWorkout(true)

    try {
      const res = await fetch(getApiUrl('/workouts'), {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          type: newWorkout.type,
          sets: Number(newWorkout.sets) || null,
          reps: Number(newWorkout.reps) || null,
          durationMinutes: Number(newWorkout.durationMinutes) || null,
          calories: Number(newWorkout.calories) || null
        })
      })

      if (!res.ok) {
        throw new Error('Failed to create workout')
      }

      const created = await res.json()
      setWorkouts(prev => [created, ...prev])
      setShowAddModal(false)
      // reset form
      setNewWorkout({
        type: 'Running',
        sets: 3,
        reps: 12,
        durationMinutes: 30,
        calories: 250
      })
    } catch (err) {
      alert(err.message || 'Could not log workout')
    } finally {
      setSubmittingWorkout(false)
    }
  }

  const handleDeleteWorkout = async (id) => {
    try {
      const res = await fetch(getApiUrl(`/workouts/${id}`), {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })

      if (res.ok || res.status === 204) {
        setWorkouts(prev => prev.filter(w => w.id !== id))
      } else {
        throw new Error('Could not delete workout')
      }
    } catch (err) {
      alert(err.message || 'Error deleting workout')
    }
  }

  const totalCalories = workouts.reduce((sum, w) => sum + (w.calories || 0), 0)
  const totalMinutes = workouts.reduce((sum, w) => sum + (w.durationMinutes || 0), 0)

  return (
    <div className="min-h-screen bg-[#0B0B0F] text-slate-100 selection:bg-[#FF4D4D] selection:text-white pb-16">
      {/* Background Glows */}
      <div className="fixed inset-0 pointer-events-none overflow-hidden">
        <div className="absolute top-0 right-1/4 w-[600px] h-[600px] bg-[#FF4D4D]/10 rounded-full blur-[160px]" />
        <div className="absolute top-1/2 left-0 w-[500px] h-[500px] bg-[#FF7A45]/8 rounded-full blur-[140px]" />
      </div>

      <div className="relative max-w-6xl mx-auto px-4 sm:px-6 z-10">
        {/* Navigation Bar */}
        <header className="flex items-center justify-between py-6 border-b border-white/[0.06] mb-8 sm:mb-10">
          <div className="flex items-center gap-3">
            <div className="w-11 h-11 rounded-2xl bg-gradient-to-tr from-[#FF4D4D] to-[#FF7A45] p-0.5 shadow-lg shadow-[#FF4D4D]/25">
              <div className="w-full h-full bg-[#1C1C22] rounded-[14px] flex items-center justify-center">
                <Flame className="w-5 h-5 text-[#FF4D4D] fill-[#FF4D4D]/20" />
              </div>
            </div>
            <div>
              <span className="text-xl font-extrabold tracking-tight text-white">
                Fit<span className="gradient-text">Track</span>
              </span>
              <span className="hidden sm:inline-block ml-2 text-[10px] font-semibold uppercase tracking-wider px-2 py-0.5 rounded-full bg-white/[0.05] text-[#9CA3AF] border border-white/[0.08]">
                Dashboard
              </span>
            </div>
          </div>

          <div className="flex items-center gap-3">
            <Link
              to="/profile"
              className="flex items-center gap-2 px-3.5 py-2 rounded-xl text-xs font-semibold text-white bg-[#1C1C22] hover:bg-[#25252D] border border-white/[0.08] hover:border-[#FF4D4D]/40 transition-all cursor-pointer group"
            >
              <div className="w-6 h-6 rounded-lg bg-gradient-to-tr from-[#FF4D4D]/20 to-[#FF7A45]/20 text-[#FF4D4D] flex items-center justify-center font-bold text-xs group-hover:bg-[#FF4D4D] group-hover:text-white transition-all">
                {user?.name ? user.name[0].toUpperCase() : 'U'}
              </div>
              <span className="font-semibold">Profile</span>
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

        {/* Hero Welcome Card */}
        <div className="card-panel-glow p-7 sm:p-9 mb-8 relative overflow-hidden">
          <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
            <div>
              <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#FF4D4D]/10 border border-[#FF4D4D]/25 text-[#FF4D4D] text-xs font-bold mb-3">
                <Sparkles className="w-3.5 h-3.5" />
                <span>JWT Authentication Active</span>
              </div>
              <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
                Welcome, {user?.name || 'Athlete'}!
              </h1>
              <p className="text-[#9CA3AF] text-sm sm:text-base mt-1 max-w-xl">
                Your account is authenticated via Spring Boot JWT. Track your daily sessions, monitor caloric output, and achieve your peak fitness goals.
              </p>
            </div>

            <button
              onClick={() => setShowAddModal(true)}
              className="gradient-btn py-3 px-5 rounded-xl text-sm font-bold flex items-center justify-center gap-2 shrink-0 cursor-pointer"
            >
              <Plus className="w-4 h-4" />
              <span>Log Workout</span>
            </button>
          </div>
        </div>

        {/* Metric Cards Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-5 mb-8">
          <div className="card-panel p-6 border border-white/[0.07] hover:border-white/[0.12] transition-colors">
            <div className="flex items-center justify-between mb-4">
              <span className="text-xs font-semibold text-[#9CA3AF] uppercase tracking-wider">Total Workouts</span>
              <div className="w-9 h-9 rounded-xl bg-[#FF4D4D]/10 text-[#FF4D4D] flex items-center justify-center">
                <Dumbbell className="w-4 h-4" />
              </div>
            </div>
            <div className="text-3xl font-extrabold text-white">{workouts.length}</div>
            <div className="text-xs text-[#9CA3AF] mt-1">Logged sessions</div>
          </div>

          <div className="card-panel p-6 border border-white/[0.07] hover:border-white/[0.12] transition-colors">
            <div className="flex items-center justify-between mb-4">
              <span className="text-xs font-semibold text-[#9CA3AF] uppercase tracking-wider">Calories Burned</span>
              <div className="w-9 h-9 rounded-xl bg-[#FF7A45]/10 text-[#FF7A45] flex items-center justify-center">
                <Flame className="w-4 h-4" />
              </div>
            </div>
            <div className="text-3xl font-extrabold text-white">{totalCalories} <span className="text-sm font-normal text-[#9CA3AF]">kcal</span></div>
            <div className="text-xs text-[#9CA3AF] mt-1">Total recorded expenditure</div>
          </div>

          <div className="card-panel p-6 border border-white/[0.07] hover:border-white/[0.12] transition-colors">
            <div className="flex items-center justify-between mb-4">
              <span className="text-xs font-semibold text-[#9CA3AF] uppercase tracking-wider">Active Time</span>
              <div className="w-9 h-9 rounded-xl bg-orange-500/10 text-orange-400 flex items-center justify-center">
                <Clock className="w-4 h-4" />
              </div>
            </div>
            <div className="text-3xl font-extrabold text-white">{totalMinutes} <span className="text-sm font-normal text-[#9CA3AF]">min</span></div>
            <div className="text-xs text-[#9CA3AF] mt-1">Time spent training</div>
          </div>

          <div className="card-panel p-6 border border-white/[0.07] hover:border-white/[0.12] transition-colors">
            <div className="flex items-center justify-between mb-4">
              <span className="text-xs font-semibold text-[#9CA3AF] uppercase tracking-wider">Security Status</span>
              <div className="w-9 h-9 rounded-xl bg-emerald-500/10 text-emerald-400 flex items-center justify-center">
                <ShieldCheck className="w-4 h-4" />
              </div>
            </div>
            <div className="text-base font-bold text-emerald-400 flex items-center gap-1.5">
              <CheckCircle2 className="w-4 h-4 text-emerald-400" />
              Token Active
            </div>
            <div className="text-xs text-[#9CA3AF] mt-1">User ID: #{user?.userId || 'N/A'}</div>
          </div>
        </div>

        {/* Live Workouts Section */}
        <div className="card-panel p-6 sm:p-8 mb-8 border border-white/[0.07]">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h2 className="text-xl font-bold text-white tracking-tight">Your Recent Workouts</h2>
              <p className="text-xs text-[#9CA3AF] mt-0.5">
                Fetched live from backend <code className="text-white/80 bg-[#121217] px-1.5 py-0.5 rounded font-mono">GET /workouts</code>
              </p>
            </div>

            <button
              onClick={() => setShowAddModal(true)}
              className="px-3.5 py-2 rounded-xl text-xs font-semibold text-white bg-[#25252D] hover:bg-[#2C2C36] border border-white/[0.1] transition-colors flex items-center gap-1.5 cursor-pointer"
            >
              <Plus className="w-3.5 h-3.5 text-[#FF7A45]" />
              <span>New Workout</span>
            </button>
          </div>

          {loadingWorkouts ? (
            <div className="text-center py-12 text-[#9CA3AF] text-sm">
              <div className="inline-block w-6 h-6 border-2 border-[#FF4D4D] border-t-transparent rounded-full animate-spin mb-3"></div>
              <div>Loading workouts from server...</div>
            </div>
          ) : workoutError ? (
            <div className="p-4 rounded-xl bg-red-500/10 border border-red-500/25 text-red-400 text-sm flex items-center gap-3">
              <AlertCircle className="w-5 h-5 shrink-0" />
              <span>{workoutError}</span>
            </div>
          ) : workouts.length === 0 ? (
            <div className="text-center py-12 px-4 rounded-2xl bg-[#121217]/50 border border-dashed border-white/[0.08]">
              <div className="w-12 h-12 rounded-2xl bg-[#FF4D4D]/10 text-[#FF4D4D] flex items-center justify-center mx-auto mb-3">
                <Dumbbell className="w-6 h-6" />
              </div>
              <h3 className="text-base font-bold text-white">No workouts recorded yet</h3>
              <p className="text-xs text-[#9CA3AF] max-w-sm mx-auto mt-1 mb-4">
                You're all set! Click the button below to log your first session to the backend database.
              </p>
              <button
                onClick={() => setShowAddModal(true)}
                className="gradient-btn py-2.5 px-4 rounded-xl text-xs font-bold inline-flex items-center gap-1.5 cursor-pointer"
              >
                <Plus className="w-3.5 h-3.5" />
                <span>Log Your First Workout</span>
              </button>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {workouts.map(item => (
                <div
                  key={item.id}
                  className="p-5 rounded-2xl bg-[#121217] border border-white/[0.05] hover:border-[#FF4D4D]/30 transition-all group flex flex-col justify-between"
                >
                  <div className="flex items-start justify-between mb-3">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-[#FF4D4D]/20 to-[#FF7A45]/20 text-[#FF4D4D] flex items-center justify-center font-bold">
                        <Dumbbell className="w-5 h-5" />
                      </div>
                      <div>
                        <h4 className="font-bold text-white text-sm">{item.type}</h4>
                        <span className="text-[11px] text-[#9CA3AF] flex items-center gap-1 mt-0.5">
                          <Calendar className="w-3 h-3" />
                          {item.date || 'Today'}
                        </span>
                      </div>
                    </div>

                    <button
                      onClick={() => handleDeleteWorkout(item.id)}
                      className="p-1.5 rounded-lg text-[#9CA3AF] hover:text-red-400 hover:bg-red-500/10 transition-colors opacity-70 group-hover:opacity-100 cursor-pointer"
                      title="Delete workout"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>

                  <div className="grid grid-cols-4 gap-2 pt-3 border-t border-white/[0.05] text-center">
                    <div>
                      <div className="text-[10px] text-[#9CA3AF] uppercase font-semibold">Sets</div>
                      <div className="text-sm font-bold text-white">{item.sets ?? '-'}</div>
                    </div>
                    <div>
                      <div className="text-[10px] text-[#9CA3AF] uppercase font-semibold">Reps</div>
                      <div className="text-sm font-bold text-white">{item.reps ?? '-'}</div>
                    </div>
                    <div>
                      <div className="text-[10px] text-[#9CA3AF] uppercase font-semibold">Mins</div>
                      <div className="text-sm font-bold text-white">{item.durationMinutes ?? '-'}</div>
                    </div>
                    <div>
                      <div className="text-[10px] text-[#9CA3AF] uppercase font-semibold">Calories</div>
                      <div className="text-sm font-bold text-[#FF7A45]">{item.calories ? `${item.calories}` : '-'}</div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Add Workout Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm animate-in fade-in duration-150">
          <div className="card-panel-glow w-full max-w-md p-6 sm:p-8 bg-[#1C1C22] border border-white/[0.1]">
            <div className="flex items-center justify-between mb-5">
              <h3 className="text-lg font-bold text-white">Log New Workout</h3>
              <button
                onClick={() => setShowAddModal(false)}
                className="text-[#9CA3AF] hover:text-white text-sm cursor-pointer"
              >
                ✕
              </button>
            </div>

            <form onSubmit={handleCreateWorkout} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-[#9CA3AF] uppercase mb-1.5">
                  Workout Name / Type
                </label>
                <input
                  type="text"
                  required
                  value={newWorkout.type}
                  onChange={e => setNewWorkout({ ...newWorkout, type: e.target.value })}
                  placeholder="e.g. Bench Press, HIIT, Running"
                  className="w-full px-3.5 py-2.5 bg-[#121217] border border-[#2A2A33] rounded-xl text-white text-sm focus:outline-none focus:border-[#FF4D4D]"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-[#9CA3AF] uppercase mb-1.5">Sets</label>
                  <input
                    type="number"
                    min="1"
                    value={newWorkout.sets}
                    onChange={e => setNewWorkout({ ...newWorkout, sets: e.target.value })}
                    className="w-full px-3.5 py-2.5 bg-[#121217] border border-[#2A2A33] rounded-xl text-white text-sm focus:outline-none focus:border-[#FF4D4D]"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-[#9CA3AF] uppercase mb-1.5">Reps</label>
                  <input
                    type="number"
                    min="1"
                    value={newWorkout.reps}
                    onChange={e => setNewWorkout({ ...newWorkout, reps: e.target.value })}
                    className="w-full px-3.5 py-2.5 bg-[#121217] border border-[#2A2A33] rounded-xl text-white text-sm focus:outline-none focus:border-[#FF4D4D]"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-[#9CA3AF] uppercase mb-1.5">Duration (mins)</label>
                  <input
                    type="number"
                    min="1"
                    value={newWorkout.durationMinutes}
                    onChange={e => setNewWorkout({ ...newWorkout, durationMinutes: e.target.value })}
                    className="w-full px-3.5 py-2.5 bg-[#121217] border border-[#2A2A33] rounded-xl text-white text-sm focus:outline-none focus:border-[#FF4D4D]"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-[#9CA3AF] uppercase mb-1.5">Calories</label>
                  <input
                    type="number"
                    min="1"
                    value={newWorkout.calories}
                    onChange={e => setNewWorkout({ ...newWorkout, calories: e.target.value })}
                    className="w-full px-3.5 py-2.5 bg-[#121217] border border-[#2A2A33] rounded-xl text-white text-sm focus:outline-none focus:border-[#FF4D4D]"
                  />
                </div>
              </div>

              <div className="pt-3 flex gap-3">
                <button
                  type="button"
                  onClick={() => setShowAddModal(false)}
                  className="flex-1 py-2.5 rounded-xl text-xs font-semibold text-[#9CA3AF] hover:text-white bg-[#121217] border border-white/[0.06] cursor-pointer"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={submittingWorkout}
                  className="flex-1 gradient-btn py-2.5 rounded-xl text-xs font-bold cursor-pointer disabled:opacity-50"
                >
                  {submittingWorkout ? 'Saving...' : 'Save Workout'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
