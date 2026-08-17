import type { Customer, CustomerStatus } from '../types/customer'
import { http } from './http'

const CUSTOMERS_PATH = '/api/customers'

/** Spring serialises id/name; the SPA uses customerId/fullName. Normalise once, here. */
interface CustomerResponse {
  id?: string
  customerId?: string
  name?: string
  fullName?: string
  email?: string
  status?: string
}

export function toCustomer(raw: CustomerResponse): Customer {
  return {
    customerId: raw.customerId ?? raw.id ?? '',
    fullName: raw.fullName ?? raw.name ?? '',
    email: raw.email ?? '',
    status: (raw.status ?? 'PROSPECT') as CustomerStatus,
  }
}

export const customersApi = {
  async list(signal?: AbortSignal): Promise<Customer[]> {
    const raw = await http<CustomerResponse[]>(CUSTOMERS_PATH, {}, signal)
    return (raw ?? []).map(toCustomer)
  },
}

export const adminApi = {
  /** ADMIN-only probe, used to prove 403 does not end the session. */
  ping(signal?: AbortSignal): Promise<{ role: string; ok: string }> {
    return http<{ role: string; ok: string }>('/api/admin/ping', {}, signal)
  },
}
