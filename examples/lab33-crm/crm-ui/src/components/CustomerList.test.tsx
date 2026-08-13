import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { CustomerList } from './CustomerList'
import { seedCustomers } from '../data/seedCustomers'

describe('CustomerList', () => {
  it('renders fixture customers by name', () => {
    render(<CustomerList customers={seedCustomers} onEdit={() => {}} />)
    expect(screen.getByRole('heading', { name: 'Amina Khan' })).toBeInTheDocument()
    expect(screen.getByRole('heading', { name: 'Ravi Singh' })).toBeInTheDocument()
  })

  it('shows the empty state when there are no customers', () => {
    render(<CustomerList customers={[]} onEdit={() => {}} />)
    expect(screen.getByRole('status')).toHaveTextContent(/no customers yet/i)
  })

  it('reports the selected customer', async () => {
    const user = userEvent.setup()
    const onEdit = vi.fn()
    render(<CustomerList customers={[seedCustomers[0]]} onEdit={onEdit} />)
    await user.click(screen.getByRole('button', { name: 'Edit' }))
    expect(onEdit).toHaveBeenCalledWith('CUS-1001')
  })
})
