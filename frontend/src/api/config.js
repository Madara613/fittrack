// Backend API base URL configuration
export const API_BASE_URL = (
  import.meta.env.VITE_API_URL || import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
).replace(/\/+$/, '')

/**
 * Returns an absolute URL pointing to the backend.
 * e.g. getApiUrl('/auth/login') -> 'http://localhost:8080/auth/login'
 */
export const getApiUrl = (endpoint) => {
  if (endpoint.startsWith('http://') || endpoint.startsWith('https://')) {
    return endpoint
  }
  const cleanPath = endpoint.startsWith('/') ? endpoint : `/${endpoint}`
  return `${API_BASE_URL}${cleanPath}`
}

/**
 * Fetch wrapper that automatically prepends API_BASE_URL.
 */
export const apiFetch = (endpoint, options = {}) => {
  const url = getApiUrl(endpoint)
  return fetch(url, options)
}

export const API_ENDPOINTS = {
  LOGIN: getApiUrl('/auth/login'),
  SIGNUP: getApiUrl('/auth/signup'),
  WORKOUTS: getApiUrl('/workouts'),
  WORKOUT_BY_ID: (id) => getApiUrl(`/workouts/${id}`),
  PROFILE: getApiUrl('/profile')
}
