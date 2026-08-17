import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from 'react'
import { authApi } from '../api/authApi'
import { onSessionExpired } from '../api/http'
import { readLabTokenClaims } from './labToken'
import { tokenStore } from './tokenStore'

export interface User {
  username: string
  role: string
}

/**
 * `checking` exists so protected content never flashes before the session is resolved.
 * Defaulting to `authenticated` would leak a frame of customer PII on every load.
 */
export type AuthState =
  | { status: 'checking' }
  | { status: 'anonymous' }
  | { status: 'authenticated'; user: User }

type AuthContextValue = AuthState & {
  login: (username: string, password: string) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [state, setState] = useState<AuthState>({ status: 'checking' })

  useEffect(() => {
    // Nothing is persisted by design, so resolution is immediate: no token means anonymous.
    // A cookie-session app would call /api/auth/me here, which is exactly why the state exists.
    const token = tokenStore.get()
    const claims = readLabTokenClaims(token)
    setState(
      token && claims
        ? { status: 'authenticated', user: { username: claims.username, role: claims.role } }
        : { status: 'anonymous' },
    )
  }, [])

  useEffect(() => {
    // A 401 on any authenticated call drops the whole app back to anonymous.
    return onSessionExpired(() => setState({ status: 'anonymous' }))
  }, [])

  const login = useCallback(async (username: string, password: string) => {
    const { accessToken } = await authApi.login(username, password)
    tokenStore.set(accessToken)
    const claims = readLabTokenClaims(accessToken)
    setState({
      status: 'authenticated',
      user: { username: claims?.username ?? username, role: claims?.role ?? 'AGENT' },
    })
  }, [])

  const logout = useCallback(() => {
    // Complete logout: drop the credential first, then the session state. Customer data is held
    // in the guarded subtree, which unmounts on `anonymous`, so the cache goes with it and the
    // back button cannot show cached PII without a fresh sign in.
    tokenStore.clear()
    setState({ status: 'anonymous' })
  }, [])

  const value = useMemo<AuthContextValue>(() => ({ ...state, login, logout }), [state, login, logout])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth requires AuthProvider')
  return ctx
}
