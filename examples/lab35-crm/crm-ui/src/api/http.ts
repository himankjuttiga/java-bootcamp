import { ApiError } from './ApiError'

const CORRELATION_ID = 'lab-request-001'

/**
 * Public host only. Never put secrets in VITE_* variables: Vite inlines them
 * into the browser bundle, so anyone with DevTools can read them.
 * Trailing slashes are stripped so `${baseUrl}/api/customers` cannot become
 * `http://localhost:8080//api/customers`, and paths keep their own `/api`
 * prefix so the base URL must not include one (that is the `/api/api` bug).
 */
const rawBaseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

export const apiBaseUrl = rawBaseUrl.replace(/\/+$/, '')

export function apiUrl(path: string): string {
  const suffix = path.startsWith('/') ? path : `/${path}`
  return `${apiBaseUrl}${suffix}`
}

/**
 * The single HTTP boundary for the SPA. Lab 36 adds Authorization here once,
 * not in every component.
 */
export async function http<T>(
  path: string,
  init: RequestInit = {},
  signal?: AbortSignal,
): Promise<T> {
  let response: Response

  try {
    response = await fetch(apiUrl(path), {
      ...init,
      signal: signal ?? init.signal,
      headers: {
        'Content-Type': 'application/json',
        'X-Correlation-Id': CORRELATION_ID,
        ...init.headers,
      },
    })
  } catch (cause) {
    // fetch only rejects for aborts and transport failures, never for 4xx/5xx.
    if (isAbort(cause, signal ?? init.signal)) {
      throw new ApiError('Request cancelled', 'abort')
    }
    throw new ApiError('Cannot reach the CRM service', 'network')
  }

  if (!response.ok) {
    throw await ApiError.from(response)
  }

  // 204 No Content has no body: parsing it as JSON would throw.
  if (response.status === 204) {
    return undefined as T
  }

  try {
    return (await response.json()) as T
  } catch {
    throw new ApiError('The CRM service returned an unreadable response', 'parse', response.status)
  }
}

function isAbort(cause: unknown, signal?: AbortSignal | null): boolean {
  if (signal?.aborted) return true
  return cause instanceof Error && cause.name === 'AbortError'
}
