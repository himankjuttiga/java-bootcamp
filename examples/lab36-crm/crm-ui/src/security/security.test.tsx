import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import App from '../App'
import { AuthProvider } from '../auth/AuthContext'
import { tokenStore } from '../auth/tokenStore'
import { isInternalPath, safeReturnPath } from '../auth/returnPath'
import { ForbiddenError, SessionExpiredError } from '../api/ApiError'
import { apiBaseUrl, onSessionExpired } from '../api/http'
import { customersApi, adminApi } from '../api/customers'

const LAB_TOKEN = 'lab.agent1.AGENT.1f2e3d'

/**
 * Web Storage is installed as a recording fake rather than read from the environment.
 *
 * Newer Node versions expose their own experimental `localStorage` global that stays undefined
 * unless --localstorage-file is passed, and it shadows jsdom's implementation, so the ambient
 * storage cannot be relied on across machines.
 *
 * This is also the stronger assertion: the fake records every write, so a token stashed under
 * ANY key is caught, not just the key a test happened to guess.
 */
interface RecordingStorage extends Storage {
  snapshot(): Record<string, string>
}

function createRecordingStorage(): RecordingStorage {
  const entries = new Map<string, string>()
  return {
    get length() {
      return entries.size
    },
    getItem: (key: string) => entries.get(key) ?? null,
    setItem: (key: string, value: string) => {
      entries.set(key, String(value))
    },
    removeItem: (key: string) => {
      entries.delete(key)
    },
    clear: () => entries.clear(),
    key: (index: number) => Array.from(entries.keys())[index] ?? null,
    snapshot: () => Object.fromEntries(entries),
  }
}

let localStore: RecordingStorage
let sessionStore: RecordingStorage

function fakeResponse(
  status: number,
  body: unknown,
  contentType: string | null = 'application/json',
): Response {
  return {
    ok: status >= 200 && status < 300,
    status,
    headers: {
      get: (name: string) => (name.toLowerCase() === 'content-type' ? contentType : null),
    },
    json: async () => body,
  } as unknown as Response
}

function stubFetch(impl: (input: string, init?: RequestInit) => Promise<Response>) {
  const spy = vi.fn(impl)
  vi.stubGlobal('fetch', spy)
  return spy
}

function headerOf(init: RequestInit | undefined, name: string): string | null {
  return new Headers(init?.headers).get(name)
}

const listBody = [
  { id: 'CUS-1001', name: 'Amina Khan', email: 'amina.khan@example.com', status: 'ACTIVE' },
  { id: 'CUS-1002', name: 'Ravi Singh', email: 'ravi.singh@example.com', status: 'PROSPECT' },
]

beforeEach(() => {
  tokenStore.clear()
  localStore = createRecordingStorage()
  sessionStore = createRecordingStorage()
  vi.stubGlobal('localStorage', localStore)
  vi.stubGlobal('sessionStorage', sessionStore)
})

afterEach(() => {
  vi.unstubAllGlobals()
  vi.restoreAllMocks()
  tokenStore.clear()
})

describe('token storage', () => {
  it('keeps the token out of Web Storage entirely', () => {
    tokenStore.set(LAB_TOKEN)

    expect(tokenStore.get()).toBe(LAB_TOKEN)
    expect(localStore.getItem('token')).toBeNull()
    expect(sessionStore.getItem('token')).toBeNull()
    expect(localStore.length).toBe(0)
    expect(sessionStore.length).toBe(0)
    // Nothing anywhere in Web Storage holds the token value, under any key.
    expect(JSON.stringify(localStore.snapshot())).not.toContain(LAB_TOKEN)
    expect(JSON.stringify(sessionStore.snapshot())).not.toContain(LAB_TOKEN)

    tokenStore.clear()
    expect(tokenStore.get()).toBeNull()
  })
})

describe('origin-scoped Authorization', () => {
  it('attaches bearer and correlation id to the CRM API origin', async () => {
    tokenStore.set(LAB_TOKEN)
    const spy = stubFetch(async () => fakeResponse(200, listBody))

    await customersApi.list()

    const [url, init] = spy.mock.calls[0] as [string, RequestInit]
    expect(url).toBe(`${apiBaseUrl}/api/customers`)
    expect(headerOf(init, 'Authorization')).toBe(`Bearer ${LAB_TOKEN}`)
    expect(headerOf(init, 'X-Correlation-Id')).toBe('lab-request-001')
  })

  it('never attaches the token to a third-party origin', async () => {
    tokenStore.set(LAB_TOKEN)
    const spy = stubFetch(async () => fakeResponse(200, { ok: true }))

    const { http } = await import('../api/http')
    await http('https://evil.example/collect')

    const [url, init] = spy.mock.calls[0] as [string, RequestInit]
    expect(url).toContain('evil.example')
    expect(headerOf(init, 'Authorization')).toBeNull()
    expect(headerOf(init, 'X-Correlation-Id')).toBeNull()
  })
})

describe('401 versus 403', () => {
  it('401 clears the session and notifies listeners', async () => {
    tokenStore.set(LAB_TOKEN)
    const expired = vi.fn()
    const unsubscribe = onSessionExpired(expired)
    stubFetch(async () => fakeResponse(401, { status: 401, message: 'Unauthorized' }))

    await expect(customersApi.list()).rejects.toBeInstanceOf(SessionExpiredError)

    expect(tokenStore.get()).toBeNull()
    expect(expired).toHaveBeenCalledTimes(1)
    unsubscribe()
  })

  it('403 keeps the session intact', async () => {
    tokenStore.set(LAB_TOKEN)
    const expired = vi.fn()
    const unsubscribe = onSessionExpired(expired)
    stubFetch(async () => fakeResponse(403, { status: 403, message: 'Forbidden' }))

    await expect(adminApi.ping()).rejects.toBeInstanceOf(ForbiddenError)

    expect(tokenStore.get()).toBe(LAB_TOKEN)
    expect(expired).not.toHaveBeenCalled()
    unsubscribe()
  })

  it('a failed login is not treated as an expired session', async () => {
    const expired = vi.fn()
    const unsubscribe = onSessionExpired(expired)
    stubFetch(async () => fakeResponse(401, { status: 401, message: 'Invalid credentials' }))

    const { authApi } = await import('../api/authApi')
    await expect(authApi.login('agent1', 'wrong')).rejects.toBeInstanceOf(SessionExpiredError)

    expect(expired).not.toHaveBeenCalled()
    unsubscribe()
  })
})

describe('open redirect defence', () => {
  it('accepts internal paths only', () => {
    expect(isInternalPath('/customers')).toBe(true)
    expect(isInternalPath('/customers?status=ACTIVE')).toBe(true)

    expect(isInternalPath('https://evil.example')).toBe(false)
    expect(isInternalPath('//evil.example')).toBe(false)
    expect(isInternalPath('/\\evil.example')).toBe(false)
    expect(isInternalPath('javascript:alert(1)')).toBe(false)
    expect(isInternalPath('customers')).toBe(false)
    expect(isInternalPath(null)).toBe(false)
  })

  it('falls back to the app root for hostile destinations', () => {
    expect(safeReturnPath('/customers')).toBe('/customers')
    expect(safeReturnPath('https://evil.example/steal')).toBe('/')
    expect(safeReturnPath('//evil.example')).toBe('/')
  })
})

describe('login, guard and logout flow', () => {
  it('guards the dashboard, signs in, then clears everything on sign out', async () => {
    const spy = stubFetch(async (input, init) => {
      if (input.includes('/api/auth/login')) {
        return fakeResponse(200, { accessToken: LAB_TOKEN, tokenType: 'Bearer' })
      }
      if (input.includes('/api/customers')) {
        expect(headerOf(init, 'Authorization')).toBe(`Bearer ${LAB_TOKEN}`)
        return fakeResponse(200, listBody)
      }
      return fakeResponse(404, { status: 404 })
    })
    const user = userEvent.setup()

    render(
      <AuthProvider>
        <App />
      </AuthProvider>,
    )

    // Anonymous: the login form is rendered and no customer data was ever requested.
    expect(await screen.findByRole('heading', { name: /sign in/i })).toBeInTheDocument()
    expect(screen.queryByText(/Amina Khan/)).not.toBeInTheDocument()
    expect(spy).not.toHaveBeenCalled()

    await user.type(screen.getByLabelText(/username/i), 'agent1')
    await user.type(screen.getByLabelText(/password/i), 'agent1')
    await user.click(screen.getByRole('button', { name: /^sign in$/i }))

    expect(await screen.findByText(/Amina Khan/)).toBeInTheDocument()
    expect(screen.getByText(/Ravi Singh/)).toBeInTheDocument()
    expect(tokenStore.get()).toBe(LAB_TOKEN)

    await user.click(screen.getByRole('button', { name: /sign out/i }))

    expect(await screen.findByRole('heading', { name: /sign in/i })).toBeInTheDocument()
    expect(tokenStore.get()).toBeNull()
    // The guarded subtree unmounted, so the customer cache went with it.
    expect(screen.queryByText(/Amina Khan/)).not.toBeInTheDocument()
    expect(localStore.length).toBe(0)
  })

  it('shows one generic message for any bad credential', async () => {
    stubFetch(async () => fakeResponse(401, { status: 401, message: 'User agent9 does not exist' }))
    const user = userEvent.setup()

    render(
      <AuthProvider>
        <App />
      </AuthProvider>,
    )

    await user.type(await screen.findByLabelText(/username/i), 'agent9')
    await user.type(screen.getByLabelText(/password/i), 'nope')
    await user.click(screen.getByRole('button', { name: /^sign in$/i }))

    const alert = await screen.findByRole('alert')
    expect(alert).toHaveTextContent(/invalid username or password/i)
    // The server's wording would confirm whether the account exists.
    expect(alert.textContent).not.toMatch(/does not exist/i)
    expect(tokenStore.get()).toBeNull()
  })

  it('keeps the user signed in when an admin probe returns 403', async () => {
    stubFetch(async (input) => {
      if (input.includes('/api/auth/login')) {
        return fakeResponse(200, { accessToken: LAB_TOKEN, tokenType: 'Bearer' })
      }
      if (input.includes('/api/admin/ping')) {
        return fakeResponse(403, { status: 403, message: 'Forbidden' })
      }
      return fakeResponse(200, listBody)
    })
    const user = userEvent.setup()

    render(
      <AuthProvider>
        <App />
      </AuthProvider>,
    )

    await user.type(await screen.findByLabelText(/username/i), 'agent1')
    await user.type(screen.getByLabelText(/password/i), 'agent1')
    await user.click(screen.getByRole('button', { name: /^sign in$/i }))
    await screen.findByText(/Amina Khan/)

    await user.click(screen.getByRole('button', { name: /check admin access/i }))

    expect(await screen.findByText(/do not have access/i)).toBeInTheDocument()
    // Still signed in: 403 is not a logout.
    expect(screen.getByText(/Amina Khan/)).toBeInTheDocument()
    expect(tokenStore.get()).toBe(LAB_TOKEN)
  })

  it('drops to the login screen when an authenticated call returns 401', async () => {
    let listCalls = 0
    stubFetch(async (input) => {
      if (input.includes('/api/auth/login')) {
        return fakeResponse(200, { accessToken: LAB_TOKEN, tokenType: 'Bearer' })
      }
      if (input.includes('/api/admin/ping')) {
        return fakeResponse(401, { status: 401, message: 'Unauthorized' })
      }
      listCalls += 1
      return fakeResponse(200, listBody)
    })
    const user = userEvent.setup()

    render(
      <AuthProvider>
        <App />
      </AuthProvider>,
    )

    await user.type(await screen.findByLabelText(/username/i), 'agent1')
    await user.type(screen.getByLabelText(/password/i), 'agent1')
    await user.click(screen.getByRole('button', { name: /^sign in$/i }))
    await screen.findByText(/Amina Khan/)
    expect(listCalls).toBe(1)

    await user.click(screen.getByRole('button', { name: /check admin access/i }))

    await waitFor(() =>
      expect(screen.getByRole('heading', { name: /sign in/i })).toBeInTheDocument(),
    )
    expect(tokenStore.get()).toBeNull()
    expect(screen.queryByText(/Amina Khan/)).not.toBeInTheDocument()
  })
})
