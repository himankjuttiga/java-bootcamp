import type { CustomerDraft } from '../types/customer'

export type FieldErrors = Partial<Record<keyof CustomerDraft, string>>

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

export function validateCustomerDraft(draft: CustomerDraft): FieldErrors {
  const errors: FieldErrors = {}

  if (!draft.fullName.trim()) {
    errors.fullName = 'Full name is required.'
  }

  if (!draft.email.trim()) {
    errors.email = 'Email is required.'
  } else if (!EMAIL_PATTERN.test(draft.email.trim())) {
    errors.email = 'Enter a valid email address.'
  }

  if (!draft.status) {
    errors.status = 'Status is required.'
  }

  return errors
}
