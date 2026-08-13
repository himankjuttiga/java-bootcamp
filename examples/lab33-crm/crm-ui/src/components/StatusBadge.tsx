import type { CustomerStatus } from '../types/customer'

const labels: Record<CustomerStatus, string> = {
  PROSPECT: 'Prospect',
  ACTIVE: 'Active',
  CLOSED: 'Closed',
}

export function StatusBadge({ status }: { status: CustomerStatus }) {
  // Status text is always rendered (never color alone) so meaning survives
  // grayscale rendering and screen readers; className only drives the color
  // treatment as a secondary cue.
  return (
    <span className={`badge badge--${status.toLowerCase()}`}>
      {labels[status]}
    </span>
  )
}
