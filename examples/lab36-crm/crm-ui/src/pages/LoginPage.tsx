import { useRef, useState, type FormEvent } from 'react'
import { useAuth } from '../auth/AuthContext'
import { safeReturnPath } from '../auth/returnPath'

/** One message for every failure. Distinct copy would confirm which usernames exist. */
const GENERIC_ERROR = 'Invalid username or password'

export function LoginPage({ returnTo = '/' }: { returnTo?: string }) {
  const { login } = useAuth()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)
  // Ref, not state: the guard must flip synchronously on the first submit, before React
  // re-renders with the disabled button.
  const submittingRef = useRef(false)

  async function handleSubmit(event: FormEvent) {
    event.preventDefault()
    if (submittingRef.current) return
    submittingRef.current = true
    setSubmitting(true)
    setError(null)

    try {
      await login(username, password)
      // Only an internal path survives this call, so a crafted returnTo cannot bounce a freshly
      // authenticated user to an attacker's host.
      const destination = safeReturnPath(returnTo)
      if (typeof window !== 'undefined' && window.location.pathname !== destination) {
        window.history.replaceState({}, '', destination)
      }
    } catch {
      // Never surface the server message here: 401 from a bad password and a missing account
      // must be indistinguishable.
      setError(GENERIC_ERROR)
    } finally {
      submittingRef.current = false
      setSubmitting(false)
    }
  }

  return (
    <form onSubmit={handleSubmit} noValidate>
      <h1>Sign in</h1>

      {error && <p role="alert">{error}</p>}

      <label htmlFor="username">Username</label>
      <input
        id="username"
        name="username"
        autoComplete="username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />

      <label htmlFor="password">Password</label>
      <input
        id="password"
        name="password"
        type="password"
        autoComplete="current-password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />

      <button type="submit" disabled={submitting}>
        {submitting ? 'Signing in…' : 'Sign in'}
      </button>
    </form>
  )
}
