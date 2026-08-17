import type { ReactNode } from 'react'
import { useAuth } from './AuthContext'
import { LoginPage } from '../pages/LoginPage'
import { LoadingPage } from '../pages/LoadingPage'
import { safeReturnPath } from './returnPath'

/**
 * A guard, not a gate. This only decides what to render; it authorizes nothing. Anyone can
 * bypass it with a URL, devtools, or curl, and the API still answers 401 without a token.
 *
 * The starter has no router dependency, so the anonymous branch renders LoginPage in place
 * rather than <Navigate to="/login" replace state={{ from }} />. The attempted path is captured
 * through safeReturnPath so an attacker-supplied destination cannot survive the login round trip.
 */
export function ProtectedRoute({ children }: { children: ReactNode }) {
  const auth = useAuth()

  if (auth.status === 'checking') return <LoadingPage />

  if (auth.status === 'anonymous') {
    const attempted =
      typeof window === 'undefined' ? '/' : `${window.location.pathname}${window.location.search}`
    return <LoginPage returnTo={safeReturnPath(attempted)} />
  }

  return <>{children}</>
}
