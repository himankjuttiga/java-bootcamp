export type ApiErrorKind = 'network' | 'http' | 'abort' | 'parse'

export type FieldErrorMap = Record<string, string>

/**
 * Spring GlobalExceptionHandler envelope (Lab 29 onward):
 * { timestamp, status, error, message, correlationId, violations: [{ field, message }] }
 */
interface SpringErrorEnvelope {
  message?: string
  error?: string
  detail?: string
  correlationId?: string
  violations?: Array<{ field?: string; message?: string }>
}

/**
 * One safe error type for the whole UI. Components switch on `kind` and
 * `status`; they never read response bodies or print stack traces.
 */
export class ApiError extends Error {
  constructor(
    message: string,
    public readonly kind: ApiErrorKind,
    public readonly status?: number,
    public readonly fieldErrors?: FieldErrorMap,
    public readonly correlationId?: string,
  ) {
    super(message)
    this.name = 'ApiError'
  }

  /** True for 400: the caller must fix input, retrying the same request is pointless. */
  get isValidation(): boolean {
    return this.status === 400
  }

  /**
   * Build an ApiError from a non-OK Response without ever leaking server internals.
   * A non-JSON body (HTML error page, proxy text) is discarded, not echoed.
   */
  static async from(response: Response): Promise<ApiError> {
    let envelope: SpringErrorEnvelope | null = null
    try {
      const contentType = response.headers.get('content-type') ?? ''
      if (contentType.includes('json')) {
        envelope = (await response.json()) as SpringErrorEnvelope
      }
    } catch {
      envelope = null
    }

    const fieldErrors: FieldErrorMap = {}
    for (const violation of envelope?.violations ?? []) {
      if (violation.field) {
        fieldErrors[violation.field] = violation.message ?? 'Invalid value.'
      }
    }

    const message =
      envelope?.message ??
      envelope?.detail ??
      ApiError.safeMessageFor(response.status)

    return new ApiError(
      message,
      'http',
      response.status,
      Object.keys(fieldErrors).length > 0 ? fieldErrors : undefined,
      envelope?.correlationId ?? undefined,
    )
  }

  /** User-facing copy for a status with no usable server message. */
  static safeMessageFor(status: number): string {
    if (status === 400) return 'Some fields need attention.'
    if (status === 404) return 'That customer could not be found.'
    if (status === 409) return 'That customer already exists.'
    if (status >= 500) return 'The CRM service had a problem. Please try again.'
    return `Request failed with status ${status}.`
  }
}
