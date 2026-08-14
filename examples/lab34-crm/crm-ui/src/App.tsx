import { useState } from 'react'
import { CustomerForm } from './components/CustomerForm'
import { seedCustomers } from './data/seedCustomers'
import type { Customer, CustomerDraft, UiMode } from './types/customer'
import { validateCustomerDraft } from './validation/customerValidation'

const emptyDraft = (): CustomerDraft => ({
  fullName: '',
  email: '',
  status: 'PROSPECT',
})

export default function App() {
  const [customers, setCustomers] = useState<Customer[]>(seedCustomers)
  const [mode, setMode] = useState<UiMode>({ type: 'list' })
  const [draft, setDraft] = useState<CustomerDraft>(emptyDraft())
  const [saving, setSaving] = useState(false)
  const [errors, setErrors] = useState(validateCustomerDraft(emptyDraft()))

  function handleSubmit() {
    const nextErrors = validateCustomerDraft(draft)
    setErrors(nextErrors)
    if (Object.keys(nextErrors).length > 0) return

    setSaving(true)
    if (mode.type === 'create') {
      // Immutable append: spread the previous array into a new one rather than
      // customers.push(...), which mutates in place and misbehaves under
      // Strict Mode's double-invoke of updater functions.
      setCustomers((prev) => [
        ...prev,
        { ...draft, customerId: crypto.randomUUID() },
      ])
      console.log('create', 'lab-request-001')
    } else if (mode.type === 'edit') {
      const editingId = mode.customerId
      // Immutable replace: map to a new array, cloning only the matching row
      // and forcing customerId back to the original so identity can't drift.
      setCustomers((prev) =>
        prev.map((c) =>
          c.customerId === editingId ? { ...c, ...draft, customerId: c.customerId } : c,
        ),
      )
      console.log('edit', editingId, 'lab-request-001')
    }
    setSaving(false)
    setMode({ type: 'list' })
    setDraft(emptyDraft())
    setErrors({})
  }

  function handleCancel() {
    setMode({ type: 'list' })
    setDraft(emptyDraft())
    setErrors({})
    console.log('cancel', 'lab-request-001')
  }

  function handleEdit(customer: Customer) {
    setDraft({
      fullName: customer.fullName,
      email: customer.email,
      status: customer.status,
    })
    setErrors({})
    setMode({ type: 'edit', customerId: customer.customerId })
  }

  function handleNewCustomer() {
    setDraft(emptyDraft())
    setErrors({})
    setMode({ type: 'create' })
  }

  return (
    <main>
      <h1>Customer Management Platform</h1>
      <ul>
        {customers.map((c) => (
          <li key={c.customerId}>
            {c.fullName} — {c.status}
            <button type="button" onClick={() => handleEdit(c)}>
              Edit
            </button>
          </li>
        ))}
      </ul>
      {mode.type !== 'list' && (
        <CustomerForm
          draft={draft}
          errors={errors}
          saving={saving}
          onChange={setDraft}
          onSubmit={handleSubmit}
          onCancel={handleCancel}
        />
      )}
      <button type="button" onClick={handleNewCustomer}>
        New customer
      </button>
    </main>
  )
}
