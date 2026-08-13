import type { Customer } from '../types/customer'
import { CustomerCard } from './CustomerCard'
import { EmptyState } from './EmptyState'

export function CustomerList({
  customers,
  onEdit,
}: {
  customers: Customer[]
  onEdit: (customerId: string) => void
}) {
  if (customers.length === 0) return <EmptyState />
  return (
    <ul>
      {customers.map((c) => (
        // key is customer.customerId, never the array index, so cards keep
        // their identity (and focus/state) if the list is sorted or filtered.
        <li key={c.customerId}>
          <CustomerCard customer={c} onEdit={onEdit} />
        </li>
      ))}
    </ul>
  )
}
