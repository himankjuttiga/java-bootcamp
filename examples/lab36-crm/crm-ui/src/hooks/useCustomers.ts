import { useCallback, useEffect, useState } from 'react'
import { customersApi } from '../api/customers'
import { ApiError } from '../api/ApiError'
import type { RequestState } from '../types/customer'

/**
 * Lives inside the guarded subtree on purpose. Logging out unmounts that subtree, so this
 * customer cache dies with it and no PII survives into the anonymous state.
 */
export function useCustomers() {
  const [state, setState] = useState<RequestState>({ kind: 'loading' })
  const [reloadToken, setReloadToken] = useState(0)

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

  const reload = useCallback(() => setReloadToken((token) => token + 1), [])

  return { state, reload }
}
