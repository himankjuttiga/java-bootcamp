import type { Customer } from '../types/customer'

/**
 * Every untrusted value is a JSX text child, so React escapes it. No dangerouslySetInnerHTML,
 * no innerHTML via refs, no HTML sinks of any kind: a customer name containing markup renders
 * as visible characters rather than executing.
 */
export function CustomerCard({
  customer,
  onEdit,
}: {
  customer: Customer
  onEdit: (customer: Customer) => void
}) {
  return (
    <li>
      <span>{customer.customerId}</span> — <span>{customer.fullName}</span> —{' '}
      <span>{customer.status}</span>
      <button type="button" onClick={() => onEdit(customer)}>
        Edit
      </button>
    </li>
  )
}
