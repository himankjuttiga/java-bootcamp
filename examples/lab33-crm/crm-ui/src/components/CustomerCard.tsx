import type { Customer } from '../types/customer'
import { StatusBadge } from './StatusBadge'

export function CustomerCard({
  customer,
  onEdit,
}: {
  customer: Customer
  onEdit: (customerId: string) => void
}) {
  const headingId = `customer-name-${customer.customerId}`
  return (
    <article
      className="card"
      aria-labelledby={headingId}
      data-testid={`card-${customer.customerId}`}
    >
      <h3 id={headingId}>{customer.fullName}</h3>
      <StatusBadge status={customer.status} />
      <p>
        <a href={`mailto:${customer.email}`}>{customer.email}</a>
      </p>
      <button type="button" onClick={() => onEdit(customer.customerId)}>
        Edit
      </button>
    </article>
  )
}
