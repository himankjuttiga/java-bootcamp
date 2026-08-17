export type CustomerStatus = 'PROSPECT' | 'ACTIVE' | 'CLOSED'

export interface Customer {
  customerId: string
  fullName: string
  email: string
  status: CustomerStatus
}

export type RequestState =
  | { kind: 'loading' }
  | { kind: 'data'; data: Customer[] }
  | { kind: 'error'; message: string }
