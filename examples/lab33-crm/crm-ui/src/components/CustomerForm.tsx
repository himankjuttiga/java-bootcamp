import type { CustomerDraft, CustomerStatus } from '../types/customer'

const statusOptions: CustomerStatus[] = ['PROSPECT', 'ACTIVE', 'CLOSED']

export function CustomerForm({
  draft,
  onChange,
  onSubmit,
}: {
  draft: CustomerDraft
  onChange: (next: CustomerDraft) => void
  onSubmit: () => void
}) {
  return (
    <form
      onSubmit={(e) => {
        e.preventDefault()
        onSubmit()
      }}
    >
      <div>
        <label htmlFor="customer-full-name">Full name</label>
        <input
          id="customer-full-name"
          value={draft.fullName}
          onChange={(e) => onChange({ ...draft, fullName: e.target.value })}
        />
      </div>
      <div>
        <label htmlFor="customer-email">Email</label>
        <input
          id="customer-email"
          type="email"
          value={draft.email}
          onChange={(e) => onChange({ ...draft, email: e.target.value })}
        />
      </div>
      <div>
        <label htmlFor="customer-status">Status</label>
        <select
          id="customer-status"
          value={draft.status}
          onChange={(e) => onChange({ ...draft, status: e.target.value as CustomerStatus })}
        >
          {statusOptions.map((status) => (
            <option key={status} value={status}>
              {status}
            </option>
          ))}
        </select>
      </div>
      <button type="submit">Save</button>
    </form>
  )
}
