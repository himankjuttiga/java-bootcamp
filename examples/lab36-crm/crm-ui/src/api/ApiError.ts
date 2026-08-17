export type ApiErrorKind = 'network' | 'http' | 'abort' | 'parse'

interface SpringErrorEnvelope {
  message?: string
  detail?: string
  correlationId?: string
}

/** One safe error type for the UI. Server internals never reach the screen. */
export class ApiError extends Error {
  constructor(
    message: string,
    public readonly kind: ApiErrorKind,
    public readonly status?: number,
    public readonly correlationId?: string,
  ) {
    super(message)
    this.name = 'ApiError'
  }

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

    return new ApiError(
      envelope?.message ?? envelope?.detail ?? ApiError.safeMessageFor(response.status),
      'http',
      response.status,
      envelope?.correlationId ?? undefined,
    )
  }

  static safeMessageFor(status: number): string {
    if (status === 404) return 'That customer could not be found.'
    if (status >= 500) return 'The CRM service had a problem. Please try again.'
    return `Request failed with status ${status}.`
  }
}

/**
 * 403 is not 401. The token is valid and the session stays; the user simply lacks the role.
 * Logging them out here sends them to re-authenticate against a wall that never opens.
 */
export class ForbiddenError extends ApiError {
  constructor(message = 'You do not have access to that.') {
    super(message, 'http', 403)
    this.name = 'ForbiddenError'
  }
}

/** 401 means the credential is gone or expired: clear the session and ask for a new sign in. */
export class SessionExpiredError extends ApiError {
  constructor(message = 'Your session has expired. Please sign in again.') {
    super(message, 'http', 401)
    this.name = 'SessionExpiredError'
  }
}
