import { ApiError, ForbiddenError, SessionExpiredError } from './ApiError'
import { tokenStore } from '../auth/tokenStore'

const CORRELATION_ID = 'lab-request-001'

const rawBase = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

/** Public host only. Secrets in VITE_* ship to every browser. */
export const apiBaseUrl = rawBase.replace(/\/+$/, '')
export const apiOrigin = new URL(apiBaseUrl).origin

/** Absolute URLs pass through; relative paths resolve against the CRM API base. */
export function resolveUrl(path: string): URL {
  if (/^https?:\/\//i.test(path)) return new URL(path)
  return new URL(`${apiBaseUrl}${path.startsWith('/') ? path : `/${path}`}`)
}

type SessionListener = () => void
const sessionExpiredListeners = new Set<SessionListener>()

/** AuthContext subscribes so a 401 anywhere drops the whole app back to anonymous. */
export function onSessionExpired(listener: SessionListener): () => void {
  sessionExpiredListeners.add(listener)
  return () => {
    sessionExpiredListeners.delete(listener)
  }
}

export async function http<T>(
  path: string,
  init: RequestInit = {},
  signal?: AbortSignal,
): Promise<T> {
  const url = resolveUrl(path)
  const isCrmApi = url.origin === apiOrigin
  const token = tokenStore.get()

  const headers = new Headers(init.headers)
  if (!headers.has('Content-Type')) headers.set('Content-Type', 'application/json')

  // Bearer token and correlation id go to the CRM API origin and nowhere else. Attaching
  // Authorization to every fetch would hand the credential to any third-party host a component
  // happens to call through this helper.
  if (isCrmApi) {
    headers.set('X-Correlation-Id', CORRELATION_ID)
    if (token) headers.set('Authorization', `Bearer ${token}`)
  }

  let response: Response
  try {
    response = await fetch(url.toString(), { ...init, signal: signal ?? init.signal, headers })
  } catch (cause) {
    const activeSignal = signal ?? init.signal
    if (activeSignal?.aborted || (cause instanceof Error && cause.name === 'AbortError')) {
      throw new ApiError('Request cancelled', 'abort')
    }
    throw new ApiError('Cannot reach the CRM service', 'network')
  }

  if (response.status === 401) {
    // Only an authenticated request coming back 401 is an expiry. A 401 from the login endpoint
    // means bad credentials and must not be reported as a lost session.
    if (token && isCrmApi) {
      tokenStore.clear()
      for (const listener of sessionExpiredListeners) listener()
    }
    throw new SessionExpiredError()
  }

  if (response.status === 403) {
    // Session preserved on purpose: signed in, simply not permitted.
    throw new ForbiddenError()
  }

  if (!response.ok) throw await ApiError.from(response)
  if (response.status === 204) return undefined as T

  try {
    return (await response.json()) as T
  } catch {
    throw new ApiError('The CRM service returned an unreadable response', 'parse', response.status)
  }
}
