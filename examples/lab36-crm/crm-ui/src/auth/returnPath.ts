/**
 * Open-redirect defence for the post-login destination.
 *
 * An attacker who can influence a returnUrl bounces a freshly authenticated user to their own
 * host. Only same-app, path-relative destinations are allowed: no scheme, no host, no
 * protocol-relative `//evil.example`, no `javascript:`.
 */
const FALLBACK = '/'

export function isInternalPath(value: string | null | undefined): boolean {
  if (!value) return false
  if (!value.startsWith('/')) return false
  // "//evil.example" and "/\evil.example" are protocol-relative URLs, not internal paths.
  if (value.startsWith('//') || value.startsWith('/\\')) return false
  if (/^[a-z][a-z0-9+.-]*:/i.test(value)) return false
  if (/[\s]/.test(value)) return false
  return true
}

export function safeReturnPath(value: string | null | undefined, fallback = FALLBACK): string {
  return isInternalPath(value) ? (value as string) : fallback
}
