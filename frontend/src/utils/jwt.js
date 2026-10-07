/**
 * Decodes the payload of a JWT token.
 * 
 * @param {string} token 
 * @returns {object|null} The parsed payload or null if invalid
 */
export function decodeJwt(token) {
  if (!token || typeof token !== 'string') return null
  const parts = token.trim().split('.')
  if (parts.length !== 3 || !parts[0] || !parts[1] || !parts[2]) {
    return null
  }

  try {
    // Convert base64url to standard base64
    let base64 = parts[1].replace(/-/g, '+').replace(/_/g, '/')
    
    // Add required padding
    const remainder = base64.length % 4
    if (remainder) {
      base64 += '='.repeat(4 - remainder)
    }

    // Decode base64 to binary string
    const decodedBinary = atob(base64)

    // Handle UTF-8 multi-byte characters
    const jsonString = decodeURIComponent(
      Array.prototype.map
        .call(decodedBinary, (c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    )

    const payload = JSON.parse(jsonString)
    return payload && typeof payload === 'object' ? payload : null
  } catch {
    return null
  }
}

/**
 * Checks whether a given token is a valid, unexpired JWT.
 * 
 * @param {string} token 
 * @returns {boolean} True if the token is valid and unexpired
 */
export function isTokenValid(token) {
  const payload = decodeJwt(token)
  if (!payload) return false

  const currentTimeInSeconds = Math.floor(Date.now() / 1000)

  // Verify expiration claim (exp)
  if (payload.exp !== undefined) {
    if (typeof payload.exp !== 'number' || isNaN(payload.exp)) {
      return false
    }
    // Check if token has expired
    if (payload.exp <= currentTimeInSeconds) {
      return false
    }
  }

  // Verify not before claim (nbf) if present
  if (payload.nbf !== undefined) {
    if (typeof payload.nbf !== 'number' || isNaN(payload.nbf)) {
      return false
    }
    if (payload.nbf > currentTimeInSeconds) {
      return false
    }
  }

  return true
}
