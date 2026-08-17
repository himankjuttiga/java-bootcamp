import { afterEach, describe, expect, it, vi } from 'vitest'
import { ApiError } from './ApiError'
import { apiBaseUrl, apiUrl } from './http'
import { customersApi } from './customers'

/** Minimal Response double: no network, no undici, deterministic headers. */
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

const aminaFromServer = {
  id: 'CUS-1001',
  name: 'Amina Khan',
  email: 'amina.khan@example.com',
  status: 'ACTIVE',
}

const raviFromServer = {
  id: 'CUS-1002',
  name: 'Ravi Singh',
  email: 'ravi.singh@example.com',
  status: 'PROSPECT',
}

afterEach(() => {
  vi.unstubAllGlobals()
  vi.restoreAllMocks()
})

describe('apiUrl', () => {
  it('joins the base URL and path with exactly one /api segment', () => {
    const url = apiUrl('/api/customers')
    expect(url).toBe(`${apiBaseUrl}/api/customers`)
    expect(url.match(/\/api\//g)).toHaveLength(1)
    expect(url).not.toContain('//api')
  })
})

describe('customersApi.list — 200', () => {
  it('requests the API base URL with the correlation header', async () => {
    const spy = stubFetch(async () => fakeResponse(200, [aminaFromServer, raviFromServer]))

    const customers = await customersApi.list()

    expect(spy).toHaveBeenCalledTimes(1)
    const [url, init] = spy.mock.calls[0] as [string, RequestInit]
    expect(url).toBe(`${apiBaseUrl}/api/customers`)
    expect((init.headers as Record<string, string>)['X-Correlation-Id']).toBe('lab-request-001')
    expect(customers).toEqual([
      {
        customerId: 'CUS-1001',
        fullName: 'Amina Khan',
        email: 'amina.khan@example.com',
        status: 'ACTIVE',
      },
      {
        customerId: 'CUS-1002',
        fullName: 'Ravi Singh',
        email: 'ravi.singh@example.com',
        status: 'PROSPECT',
      },
    ])
  })

  it('accepts the customerId/fullName shape as well as id/name', async () => {
    stubFetch(async () =>
      fakeResponse(200, [
        { customerId: 'CUS-1001', fullName: 'Amina Khan', email: 'a@example.com', status: 'ACTIVE' },
      ]),
    )

    const [customer] = await customersApi.list()

    expect(customer.customerId).toBe('CUS-1001')
    expect(customer.fullName).toBe('Amina Khan')
  })

  it('returns an empty array for an empty list', async () => {
    stubFetch(async () => fakeResponse(200, []))
    await expect(customersApi.list()).resolves.toEqual([])
  })
})

describe('customersApi.create — 201', () => {
  it('POSTs the draft and returns the server record', async () => {
    const spy = stubFetch(async () => fakeResponse(201, aminaFromServer))

    const created = await customersApi.create(
      { fullName: 'Amina Khan', email: 'amina.khan@example.com', status: 'ACTIVE' },
      'CUS-1001',
    )

    const [, init] = spy.mock.calls[0] as [string, RequestInit]
    expect(init.method).toBe('POST')
    expect(JSON.parse(init.body as string)).toEqual({
      id: 'CUS-1001',
      name: 'Amina Khan',
      email: 'amina.khan@example.com',
      status: 'ACTIVE',
    })
    expect(created.customerId).toBe('CUS-1001')
  })
})

describe('customersApi — 400 field errors', () => {
  it('maps Spring violations onto form field names', async () => {
    stubFetch(async () =>
      fakeResponse(400, {
        status: 400,
        message: 'Validation failed',
        correlationId: 'lab-request-001',
        violations: [
          { field: 'email', message: 'must be a well-formed email address' },
          { field: 'name', message: 'must not be blank' },
        ],
      }),
    )

    const error = await customersApi
      .create({ fullName: '', email: 'not-an-email', status: 'ACTIVE' }, 'CUS-4242')
      .catch((e: unknown) => e)

    expect(error).toBeInstanceOf(ApiError)
    const apiError = error as ApiError
    expect(apiError.isValidation).toBe(true)
    expect(apiError.status).toBe(400)
    expect(apiError.fieldErrors).toEqual({
      email: 'must be a well-formed email address',
      fullName: 'must not be blank',
    })
    expect(apiError.correlationId).toBe('lab-request-001')
  })
})

describe('customersApi — 404 and 500', () => {
  it('turns 404 into an ApiError without leaking the body', async () => {
    stubFetch(async () => fakeResponse(404, { status: 404, message: 'Customer not found: CUS-9999' }))

    const error = (await customersApi.get('CUS-9999').catch((e: unknown) => e)) as ApiError

    expect(error).toBeInstanceOf(ApiError)
    expect(error.kind).toBe('http')
    expect(error.status).toBe(404)
    expect(error.isValidation).toBe(false)
  })

  it('turns 500 into a safe ApiError with no stack trace text', async () => {
    stubFetch(async () => fakeResponse(500, { status: 500, message: 'Unexpected error' }))

    const error = (await customersApi.list().catch((e: unknown) => e)) as ApiError

    expect(error.kind).toBe('http')
    expect(error.status).toBe(500)
    expect(error.message).toBe('Unexpected error')
    expect(error.message).not.toMatch(/at [\w.]+\(/)
  })

  it('falls back to safe copy when the error body is not JSON', async () => {
    stubFetch(async () => fakeResponse(500, '<html>Proxy Error</html>', 'text/html'))

    const error = (await customersApi.list().catch((e: unknown) => e)) as ApiError

    expect(error.message).toBe('The CRM service had a problem. Please try again.')
    expect(error.message).not.toContain('html')
  })
})

describe('customersApi — network and abort', () => {
  it('maps a transport failure to kind network', async () => {
    stubFetch(async () => {
      throw new TypeError('Failed to fetch')
    })

    const error = (await customersApi.list().catch((e: unknown) => e)) as ApiError

    expect(error).toBeInstanceOf(ApiError)
    expect(error.kind).toBe('network')
    expect(error.message).toBe('Cannot reach the CRM service')
  })

  it('maps an aborted request to kind abort, not to an error toast', async () => {
    stubFetch(async () => {
      throw Object.assign(new Error('The operation was aborted'), { name: 'AbortError' })
    })

    const controller = new AbortController()
    controller.abort()

    const error = (await customersApi.list(controller.signal).catch((e: unknown) => e)) as ApiError

    expect(error.kind).toBe('abort')
    expect(error.status).toBeUndefined()
  })

  it('passes the AbortSignal through to fetch', async () => {
    const spy = stubFetch(async () => fakeResponse(200, []))
    const controller = new AbortController()

    await customersApi.list(controller.signal)

    const [, init] = spy.mock.calls[0] as [string, RequestInit]
    expect(init.signal).toBe(controller.signal)
  })
})

describe('customersApi.update — 204', () => {
  it('does not JSON-parse an empty body', async () => {
    stubFetch(async () => fakeResponse(204, undefined, null))

    const updated = await customersApi.update('CUS-1002', {
      fullName: 'Ravi K. Singh',
      email: 'ravi.singh@example.com',
      status: 'PROSPECT',
    })

    expect(updated).toEqual({
      customerId: 'CUS-1002',
      fullName: 'Ravi K. Singh',
      email: 'ravi.singh@example.com',
      status: 'PROSPECT',
    })
  })
})
