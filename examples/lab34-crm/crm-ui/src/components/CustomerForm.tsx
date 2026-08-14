import type { CustomerDraft, CustomerStatus } from '../types/customer'
import type { FieldErrors } from '../validation/customerValidation'

const statusOptions: CustomerStatus[] = ['PROSPECT', 'ACTIVE', 'CLOSED']

export function CustomerForm({
  draft,
  errors,
  saving,
  onChange,
  onSubmit,
  onCancel,
}: {
  draft: CustomerDraft
  errors: FieldErrors
  saving: boolean
  onChange: (next: CustomerDraft) => void
  onSubmit: () => void
  onCancel: () => void
}) {
  return (
    <form
      noValidate
      onSubmit={(e) => {
        e.preventDefault()
        onSubmit()
      }}
    >
      <label htmlFor="fullName">Full name</label>
      <input
        id="fullName"
        value={draft.fullName}
        onChange={(e) => onChange({ ...draft, fullName: e.target.value })}
      />
      {errors.fullName && <p role="alert">{errors.fullName}</p>}

      <label htmlFor="email">Email</label>
      <input
        id="email"
        type="email"
        value={draft.email}
        onChange={(e) => onChange({ ...draft, email: e.target.value })}
      />
      {errors.email && <p role="alert">{errors.email}</p>}

      <label htmlFor="status">Status</label>
      <select
        id="status"
        value={draft.status}
        onChange={(e) => onChange({ ...draft, status: e.target.value as CustomerStatus })}
      >
        {statusOptions.map((status) => (
          <option key={status} value={status}>
            {status}
          </option>
        ))}
      </select>
      {errors.status && <p role="alert">{errors.status}</p>}

      <button type="submit" disabled={saving}>
        Save
      </button>
      <button type="button" onClick={onCancel}>
        Cancel
      </button>
    </form>
  )
}
