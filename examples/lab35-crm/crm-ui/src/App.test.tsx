import { render, screen, waitFor, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { afterEach, describe, expect, it, vi } from 'vitest'
import App from './App'

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

const listBody = [
  { id: 'CUS-1001', name: 'Amina Khan', email: 'amina.khan@example.com', status: 'ACTIVE' },
  { id: 'CUS-1002', name: 'Ravi Singh', email: 'ravi.singh@example.com', status: 'PROSPECT' },
]

function stubFetch(impl: (input: string, init?: RequestInit) => Promise<Response>) {
  const spy = vi.fn(impl)
  vi.stubGlobal('fetch', spy)
  return spy
}

afterEach(() => {
  vi.unstubAllGlobals()
  vi.restoreAllMocks()
})

describe('App request states', () => {
  it('shows a loading state and then the customers from the API', async () => {
    stubFetch(async () => fakeResponse(200, listBody))

    render(<App />)

    expect(screen.getByRole('status')).toHaveTextContent(/loading customers/i)
    expect(await screen.findByText(/Amina Khan/)).toBeInTheDocument()
    expect(screen.getByText(/Ravi Singh/)).toBeInTheDocument()
    expect(screen.getAllByRole('listitem')).toHaveLength(2)
    expect(screen.queryByRole('status')).not.toBeInTheDocument()
  })

  it('shows an empty state, not an error, when the API returns no rows', async () => {
    stubFetch(async () => fakeResponse(200, []))

    render(<App />)

    expect(await screen.findByText(/no customers yet/i)).toBeInTheDocument()
    expect(screen.queryByRole('alert')).not.toBeInTheDocument()
    expect(screen.queryByRole('list')).not.toBeInTheDocument()
  })

  it('shows an error state with a working Retry when the API is unreachable', async () => {
    let attempt = 0
    stubFetch(async () => {
      attempt += 1
      if (attempt === 1) throw new TypeError('Failed to fetch')
      return fakeResponse(200, listBody)
    })
    const user = userEvent.setup()

    render(<App />)

    expect(await screen.findByRole('alert')).toHaveTextContent(/cannot reach the crm service/i)
    await user.click(screen.getByRole('button', { name: /retry/i }))

    expect(await screen.findByText(/Amina Khan/)).toBeInTheDocument()
    expect(screen.queryByRole('alert')).not.toBeInTheDocument()
  })

  it('aborts the in-flight load when the component unmounts', async () => {
    const spy = stubFetch(
      () => new Promise<Response>(() => {}), // never settles
    )

    const view = render(<App />)
    const [, init] = spy.mock.calls[0] as [string, RequestInit]
    expect(init.signal?.aborted).toBe(false)

    view.unmount()

    expect(init.signal?.aborted).toBe(true)
  })
})

describe('App write flows', () => {
  it('maps a 400 field error onto the Email field and leaves the list unchanged', async () => {
    stubFetch(async (_input, init) => {
      if (init?.method === 'POST') {
        return fakeResponse(400, {
          status: 400,
          message: 'Validation failed',
          correlationId: 'lab-request-001',
          violations: [{ field: 'email', message: 'must be a well-formed email address' }],
        })
      }
      return fakeResponse(200, listBody)
    })
    const user = userEvent.setup()

    render(<App />)
    await screen.findByText(/Amina Khan/)
    await user.click(screen.getByRole('button', { name: /new customer/i }))
    await user.type(screen.getByLabelText(/full name/i), 'Nina Torres')
    await user.type(screen.getByLabelText(/email/i), 'nina@example.com')
    await user.click(screen.getByRole('button', { name: /^save$/i }))

    const alerts = await screen.findAllByRole('alert')
    expect(alerts.some((a) => /well-formed email/i.test(a.textContent ?? ''))).toBe(true)
    expect(screen.getAllByRole('listitem')).toHaveLength(2)
    expect(screen.getByLabelText(/full name/i)).toHaveValue('Nina Torres')
  })

  it('adds the server-returned record on a successful create', async () => {
    stubFetch(async (_input, init) => {
      if (init?.method === 'POST') {
        return fakeResponse(201, {
          id: 'CUS-4242',
          name: 'Nina Torres',
          email: 'nina@example.com',
          status: 'PROSPECT',
        })
      }
      return fakeResponse(200, listBody)
    })
    const user = userEvent.setup()

    render(<App />)
    await screen.findByText(/Amina Khan/)
    await user.click(screen.getByRole('button', { name: /new customer/i }))
    await user.type(screen.getByLabelText(/full name/i), 'Nina Torres')
    await user.type(screen.getByLabelText(/email/i), 'nina@example.com')
    await user.click(screen.getByRole('button', { name: /^save$/i }))

    expect(await screen.findByText(/CUS-4242/)).toBeInTheDocument()
    expect(screen.getAllByRole('listitem')).toHaveLength(3)
    expect(screen.queryByLabelText(/full name/i)).not.toBeInTheDocument()
  })

  it('sends one POST for a double-clicked Save', async () => {
    let releasePost: (value: Response) => void = () => {}
    const spy = stubFetch(async (_input, init) => {
      if (init?.method === 'POST') {
        return new Promise<Response>((resolve) => {
          releasePost = resolve
        })
      }
      return fakeResponse(200, listBody)
    })
    const user = userEvent.setup()

    render(<App />)
    await screen.findByText(/Amina Khan/)
    await user.click(screen.getByRole('button', { name: /new customer/i }))
    await user.type(screen.getByLabelText(/full name/i), 'Nina Torres')
    await user.type(screen.getByLabelText(/email/i), 'nina@example.com')

    const save = screen.getByRole('button', { name: /^save$/i })
    await user.click(save)
    await user.click(save)

    const posts = spy.mock.calls.filter(([, init]) => (init as RequestInit)?.method === 'POST')
    expect(posts).toHaveLength(1)
    expect(screen.getByRole('button', { name: /saving/i })).toBeDisabled()

    releasePost(
      fakeResponse(201, {
        id: 'CUS-4242',
        name: 'Nina Torres',
        email: 'nina@example.com',
        status: 'PROSPECT',
      }),
    )
    await waitFor(() => expect(screen.getAllByRole('listitem')).toHaveLength(3))
  })

  it('updates Ravi through PUT and keeps the list length', async () => {
    stubFetch(async (input, init) => {
      if (init?.method === 'PUT') {
        expect(input).toContain('/api/customers/CUS-1002')
        return fakeResponse(200, {
          id: 'CUS-1002',
          name: 'Ravi K. Singh',
          email: 'ravi.singh@example.com',
          status: 'PROSPECT',
        })
      }
      return fakeResponse(200, listBody)
    })
    const user = userEvent.setup()

    render(<App />)
    const raviRow = (await screen.findByText(/Ravi Singh/)).closest('li')!
    await user.click(within(raviRow).getByRole('button', { name: /edit/i }))
    const nameInput = screen.getByLabelText(/full name/i)
    await user.clear(nameInput)
    await user.type(nameInput, 'Ravi K. Singh')
    await user.click(screen.getByRole('button', { name: /^save$/i }))

    expect(await screen.findByText(/Ravi K. Singh/)).toBeInTheDocument()
    expect(screen.getAllByRole('listitem')).toHaveLength(2)
  })
})
