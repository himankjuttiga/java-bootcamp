import { useState } from 'react'
import { ApiError } from './api/ApiError'
import { CustomerForm } from './components/CustomerForm'
import { useCustomers } from './hooks/useCustomers'
import type { Customer, CustomerDraft, UiMode } from './types/customer'
import { validateCustomerDraft, type FieldErrors } from './validation/customerValidation'

const emptyDraft = (): CustomerDraft => ({
  fullName: '',
  email: '',
  status: 'PROSPECT',
})

export default function App() {
  const { state, reload, saving, createCustomer, updateCustomer } = useCustomers()
  const [mode, setMode] = useState<UiMode>({ type: 'list' })
  const [draft, setDraft] = useState<CustomerDraft>(emptyDraft())
  const [errors, setErrors] = useState<FieldErrors>({})
  const [formError, setFormError] = useState<string | null>(null)

  function resetForm() {
    setDraft(emptyDraft())
    setErrors({})
    setFormError(null)
  }

  async function handleSubmit() {
    const nextErrors = validateCustomerDraft(draft)
    setErrors(nextErrors)
    setFormError(null)
    if (Object.keys(nextErrors).length > 0) return

    try {
      const saved =
        mode.type === 'edit'
          ? await updateCustomer(mode.customerId, draft)
          : await createCustomer(draft)

      // null means a save was already in flight: the duplicate submit was swallowed.
      if (saved === null) return

      setMode({ type: 'list' })
      resetForm()
    } catch (error) {
      // A 400 is not retryable: show what the server rejected, keep the form open
      // and leave the cached list untouched.
      if (error instanceof ApiError && error.isValidation) {
        setErrors((error.fieldErrors ?? {}) as FieldErrors)
        setFormError(error.fieldErrors ? null : error.message)
        return
      }
      setFormError(error instanceof Error ? error.message : 'Save failed.')
    }
  }

  function handleEdit(customer: Customer) {
    setDraft({
      fullName: customer.fullName,
      email: customer.email,
      status: customer.status,
    })
    setErrors({})
    setFormError(null)
    setMode({ type: 'edit', customerId: customer.customerId })
  }

  function handleNewCustomer() {
    resetForm()
    setMode({ type: 'create' })
  }

  function handleCancel() {
    setMode({ type: 'list' })
    resetForm()
  }

  return (
    <main>
      <h1>Customer Management Platform</h1>

      {state.kind === 'loading' && <p role="status">Loading customers…</p>}

      {state.kind === 'error' && (
        <section>
          <p role="alert">{state.message}</p>
          <button type="button" onClick={reload}>
            Retry
          </button>
        </section>
      )}

      {state.kind === 'data' && state.data.length === 0 && (
        <p>No customers yet. Create your first customer to get started.</p>
      )}

      {state.kind === 'data' && state.data.length > 0 && (
        <ul>
          {state.data.map((customer) => (
            <li key={customer.customerId}>
              {customer.customerId} — {customer.fullName} — {customer.status}
              <button type="button" onClick={() => handleEdit(customer)}>
                Edit
              </button>
            </li>
          ))}
        </ul>
      )}

      {mode.type !== 'list' && (
        <CustomerForm
          draft={draft}
          errors={errors}
          formError={formError}
          saving={saving}
          onChange={setDraft}
          onSubmit={handleSubmit}
          onCancel={handleCancel}
        />
      )}

      {state.kind === 'data' && mode.type === 'list' && (
        <button type="button" onClick={handleNewCustomer}>
          New customer
        </button>
      )}
    </main>
  )
}
