import { useState } from 'react'
import { adminApi } from './api/customers'
import { ForbiddenError } from './api/ApiError'
import { CustomerCard } from './components/CustomerCard'
import { ProtectedRoute } from './auth/ProtectedRoute'
import { useAuth } from './auth/AuthContext'
import { useCustomers } from './hooks/useCustomers'

function Dashboard() {
  const auth = useAuth()
  const { state, reload } = useCustomers()
  const [adminMessage, setAdminMessage] = useState<string | null>(null)
  const user = auth.status === 'authenticated' ? auth.user : null

  async function checkAdminAccess() {
    setAdminMessage(null)
    try {
      const result = await adminApi.ping()
      setAdminMessage(`Admin access confirmed (${result.role}).`)
    } catch (error) {
      // 403 keeps the session. Only a 401 signs the user out, handled centrally in http.ts.
      if (error instanceof ForbiddenError) {
        setAdminMessage(error.message)
        return
      }
      setAdminMessage(error instanceof Error ? error.message : 'Request failed.')
    }
  }

  return (
    <main>
      <h1>Customer Management Platform</h1>

      <p>
        Signed in as <span>{user?.username}</span> ({user?.role})
      </p>

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
            <CustomerCard key={customer.customerId} customer={customer} onEdit={() => {}} />
          ))}
        </ul>
      )}

      {/* Rendered for everyone on purpose: hiding it would be UI tidiness, not authorization.
          Spring answers 403 for a non-ADMIN token, which is the control that actually holds. */}
      <button type="button" onClick={checkAdminAccess}>
        Check admin access
      </button>
      {adminMessage && <p role="alert">{adminMessage}</p>}

      <button type="button" onClick={auth.logout}>
        Sign out
      </button>
    </main>
  )
}

export default function App() {
  return (
    <ProtectedRoute>
      <Dashboard />
    </ProtectedRoute>
  )
}
