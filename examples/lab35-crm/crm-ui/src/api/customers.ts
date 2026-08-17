import type { Customer, CustomerDraft, CustomerStatus } from '../types/customer'
import { ApiError, type FieldErrorMap } from './ApiError'
import { http } from './http'

const CUSTOMERS_PATH = '/api/customers'

/**
 * The Spring CRM model (Lab 29 onward) serialises `id` / `name`, while the SPA
 * types use `customerId` / `fullName`. Normalise once, here, so no component
 * ever has to know both shapes. Documented in docs/api-integration-notes.md.
 */
interface CustomerResponse {
  id?: string
  customerId?: string
  name?: string
  fullName?: string
  email?: string
  status?: string
}

/** Server violation field names -> form field names. */
const FIELD_ALIASES: Record<string, string> = {
  name: 'fullName',
  fullName: 'fullName',
  email: 'email',
  status: 'status',
  id: 'customerId',
}

export function toCustomer(raw: CustomerResponse): Customer {
  return {
    customerId: raw.customerId ?? raw.id ?? '',
    fullName: raw.fullName ?? raw.name ?? '',
    email: raw.email ?? '',
    status: (raw.status ?? 'PROSPECT') as CustomerStatus,
  }
}

/** Payload the Spring controller expects; `id` is client-supplied in this lab. */
function toRequestBody(draft: CustomerDraft, customerId: string) {
  return {
    id: customerId,
    name: draft.fullName.trim(),
    email: draft.email.trim(),
    status: draft.status,
  }
}

function translateFieldErrors(fieldErrors?: FieldErrorMap): FieldErrorMap | undefined {
  if (!fieldErrors) return undefined
  const translated: FieldErrorMap = {}
  for (const [field, message] of Object.entries(fieldErrors)) {
    translated[FIELD_ALIASES[field] ?? field] = message
  }
  return translated
}

/** Re-throw a 400 with form-shaped field names; everything else passes through. */
function rethrowWithFormFields(error: unknown): never {
  if (error instanceof ApiError && error.isValidation) {
    throw new ApiError(
      error.message,
      error.kind,
      error.status,
      translateFieldErrors(error.fieldErrors),
      error.correlationId,
    )
  }
  throw error
}

export const customersApi = {
  async list(signal?: AbortSignal): Promise<Customer[]> {
    const raw = await http<CustomerResponse[]>(CUSTOMERS_PATH, {}, signal)
    return (raw ?? []).map(toCustomer)
  },

  async get(customerId: string, signal?: AbortSignal): Promise<Customer> {
    const raw = await http<CustomerResponse>(
      `${CUSTOMERS_PATH}/${encodeURIComponent(customerId)}`,
      {},
      signal,
    )
    return toCustomer(raw)
  },

  async create(draft: CustomerDraft, customerId: string): Promise<Customer> {
    try {
      const raw = await http<CustomerResponse>(CUSTOMERS_PATH, {
        method: 'POST',
        body: JSON.stringify(toRequestBody(draft, customerId)),
      })
      return toCustomer(raw)
    } catch (error) {
      rethrowWithFormFields(error)
    }
  },

  async update(customerId: string, draft: CustomerDraft): Promise<Customer> {
    try {
      const raw = await http<CustomerResponse | undefined>(
        `${CUSTOMERS_PATH}/${encodeURIComponent(customerId)}`,
        {
          method: 'PUT',
          body: JSON.stringify(toRequestBody(draft, customerId)),
        },
      )
      // A 204 update returns no body: fall back to the draft we just sent.
      return raw ? toCustomer(raw) : { customerId, ...draft }
    } catch (error) {
      rethrowWithFormFields(error)
    }
  },
}

/** Client-side draft id: the Lab 29 controller requires a non-blank `id`. */
export function nextCustomerId(): string {
  return `CUS-${Math.floor(2000 + Math.random() * 8000)}`
}
