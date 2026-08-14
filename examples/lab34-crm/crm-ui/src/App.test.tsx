import { render, screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import App from './App'

describe('App flows', () => {
  it('shows seed customers', () => {
    render(<App />)
    expect(screen.getByText(/Amina Khan/i)).toBeInTheDocument()
    expect(screen.getByText(/Ravi Singh/i)).toBeInTheDocument()
  })

  it('opens create form with empty fields', async () => {
    const user = userEvent.setup()
    render(<App />)
    await user.click(screen.getByRole('button', { name: /new customer/i }))
    expect(screen.getByLabelText(/full name/i)).toHaveValue('')
    expect(screen.getByLabelText(/email/i)).toHaveValue('')
  })

  it('creates a valid customer', async () => {
    const user = userEvent.setup()
    render(<App />)
    await user.click(screen.getByRole('button', { name: /new customer/i }))
    await user.type(screen.getByLabelText(/full name/i), 'Nina Torres')
    await user.type(screen.getByLabelText(/email/i), 'nina.torres@example.com')
    await user.click(screen.getByRole('button', { name: /^save$/i }))
    expect(screen.getByText(/Nina Torres/i)).toBeInTheDocument()
    expect(screen.getAllByRole('listitem')).toHaveLength(3)
  })

  it('blocks an invalid create and leaves the list unchanged', async () => {
    const user = userEvent.setup()
    render(<App />)
    await user.click(screen.getByRole('button', { name: /new customer/i }))
    await user.click(screen.getByRole('button', { name: /^save$/i }))
    const alerts = await screen.findAllByRole('alert')
    expect(alerts.some((a) => /full name is required/i.test(a.textContent ?? ''))).toBe(true)
    expect(screen.getAllByRole('listitem')).toHaveLength(2)
  })

  it('discards the draft on cancel', async () => {
    const user = userEvent.setup()
    render(<App />)
    await user.click(screen.getByRole('button', { name: /new customer/i }))
    await user.type(screen.getByLabelText(/full name/i), 'Temp Name')
    await user.click(screen.getByRole('button', { name: /cancel/i }))
    expect(screen.queryByText(/Temp Name/i)).not.toBeInTheDocument()
    expect(screen.getAllByRole('listitem')).toHaveLength(2)
    await user.click(screen.getByRole('button', { name: /new customer/i }))
    expect(screen.getByLabelText(/full name/i)).toHaveValue('')
  })

  it('edits Ravi and saves the updated name', async () => {
    const user = userEvent.setup()
    render(<App />)
    const raviRow = screen.getByText(/Ravi Singh/i).closest('li')!
    await user.click(within(raviRow).getByRole('button', { name: /edit/i }))
    const nameInput = screen.getByLabelText(/full name/i)
    await user.clear(nameInput)
    await user.type(nameInput, 'Ravi K. Singh')
    await user.click(screen.getByRole('button', { name: /^save$/i }))
    expect(screen.getByText(/Ravi K. Singh/i)).toBeInTheDocument()
    expect(screen.getByText(/Amina Khan/i)).toBeInTheDocument()
    expect(screen.getAllByRole('listitem')).toHaveLength(2)
  })

  it('rejects an invalid email on create', async () => {
    const user = userEvent.setup()
    render(<App />)
    await user.click(screen.getByRole('button', { name: /new customer/i }))
    await user.type(screen.getByLabelText(/full name/i), 'Bad Email Person')
    await user.type(screen.getByLabelText(/email/i), 'not-an-email')
    await user.click(screen.getByRole('button', { name: /^save$/i }))
    expect(await screen.findByRole('alert')).toHaveTextContent(/valid email/i)
    expect(screen.getAllByRole('listitem')).toHaveLength(2)
  })
})
