import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { CustomerCard } from '../components/CustomerCard'
import type { Customer } from '../types/customer'

const amina: Customer = {
  customerId: 'CUS-1001',
  fullName: 'Amina Khan',
  email: 'amina.khan@example.com',
  status: 'ACTIVE',
}

describe('XSS posture', () => {
  it('renders a malicious fullName as text, never as HTML', () => {
    const { container } = render(
      <CustomerCard
        customer={{ ...amina, fullName: '<img src=x onerror=alert(1) />' }}
        onEdit={() => {}}
      />,
    )

    // The attack string is visible characters, and no element was created from it.
    expect(screen.getByText(/<img src=x onerror=alert\(1\) \/>/)).toBeInTheDocument()
    expect(container.querySelector('img')).toBeNull()
    expect(container.querySelector('script')).toBeNull()
  })

  it('renders script markup in a name literally', () => {
    const { container } = render(
      <CustomerCard
        customer={{ ...amina, fullName: '<script>fetch("https://evil.example")</script>' }}
        onEdit={() => {}}
      />,
    )

    expect(screen.getByText(/<script>/)).toBeInTheDocument()
    expect(container.querySelector('script')).toBeNull()
  })

  it('escapes markup in every untrusted field, not just the name', () => {
    const { container } = render(
      <CustomerCard
        customer={{ ...amina, customerId: '<b>CUS-1001</b>', status: 'ACTIVE' }}
        onEdit={() => {}}
      />,
    )

    expect(screen.getByText(/<b>CUS-1001<\/b>/)).toBeInTheDocument()
    expect(container.querySelector('b')).toBeNull()
  })
})
