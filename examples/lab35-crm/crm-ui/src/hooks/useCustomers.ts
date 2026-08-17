import { useCallback, useEffect, useRef, useState } from 'react'
import { customersApi, nextCustomerId } from '../api/customers'
import { ApiError } from '../api/ApiError'
import type { Customer, CustomerDraft, RequestState } from '../types/customer'

export function useCustomers() {
  const [state, setState] = useState<RequestState>({ kind: 'loading' })
  const [reloadToken, setReloadToken] = useState(0)
  const [saving, setSaving] = useState(false)
  // Ref, not state: the guard must flip synchronously on the first click,
  // before React has re-rendered with the disabled button.
  const savingRef = useRef(false)

  useEffect(() => {
    const controller = new AbortController()
    let cancelled = false
    setState({ kind: 'loading' })

    customersApi
      .list(controller.signal)
      .then((data) => {
        if (!cancelled) setState({ kind: 'data', data })
      })
      .catch((error: unknown) => {
        // An abort is our own cancellation, not a failure: no error UI, no toast.
        if (error instanceof ApiError && error.kind === 'abort') return
        if (controller.signal.aborted || cancelled) return
        setState({
          kind: 'error',
          message: error instanceof Error ? error.message : 'Unknown error',
        })
      })

    return () => {
      cancelled = true
      controller.abort()
    }
  }, [reloadToken])

  const reload = useCallback(() => {
    setReloadToken((token) => token + 1)
  }, [])

  /** Shared write path: one in-flight save at a time, list updated from the server record. */
  const runSave = useCallback(
    async (
      call: () => Promise<Customer>,
      merge: (previous: Customer[], saved: Customer) => Customer[],
    ): Promise<Customer | null> => {
      if (savingRef.current) return null
      savingRef.current = true
      setSaving(true)
      try {
        const saved = await call()
        setState((previous) =>
          previous.kind === 'data'
            ? { kind: 'data', data: merge(previous.data, saved) }
            : previous,
        )
        return saved
      } finally {
        savingRef.current = false
        setSaving(false)
      }
    },
    [],
  )

  const createCustomer = useCallback(
    (draft: CustomerDraft) =>
      runSave(
        () => customersApi.create(draft, nextCustomerId()),
        (previous, saved) => [...previous, saved],
      ),
    [runSave],
  )

  const updateCustomer = useCallback(
    (customerId: string, draft: CustomerDraft) =>
      runSave(
        () => customersApi.update(customerId, draft),
        (previous, saved) =>
          previous.map((customer) =>
            customer.customerId === customerId
              ? { ...saved, customerId: saved.customerId || customerId }
              : customer,
          ),
      ),
    [runSave],
  )

  return { state, reload, saving, createCustomer, updateCustomer }
}
