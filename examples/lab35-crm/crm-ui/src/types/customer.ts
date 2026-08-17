export type CustomerStatus = 'PROSPECT' | 'ACTIVE' | 'CLOSED'

export interface Customer {
  customerId: string
  fullName: string
  email: string
  status: CustomerStatus
}

export interface CustomerDraft {
  fullName: string
  email: string
  status: CustomerStatus
}

export type UiMode =
  | { type: 'list' }
  | { type: 'create' }
  | { type: 'edit'; customerId: string }

/**
 * Lab 34 kept customers in component state as the source of truth.
 * From Lab 35 on, component state is a cache of server records, so the
 * list needs an explicit request state instead of a bare array.
 *
 * "empty" is not a separate kind: it is kind === 'data' with data.length === 0.
 */
export type RequestState =
  | { kind: 'loading' }
  | { kind: 'data'; data: Customer[] }
  | { kind: 'error'; message: string }
