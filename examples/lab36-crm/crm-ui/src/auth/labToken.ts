/**
 * The lab API issues `lab.<subject>.<role>.<signature>` (see JwtService in the Spring project).
 *
 * DISPLAY ONLY. Nothing read here is trusted: the signature is not verified client-side and
 * could be forged by anyone with devtools. Every authorization decision is made by Spring
 * Security on the server; we read the role purely so the UI can label the session.
 */
export interface LabTokenClaims {
  username: string
  role: string
}

export function readLabTokenClaims(token: string | null): LabTokenClaims | null {
  if (!token) return null
  const parts = token.split('.')
  if (parts.length < 4 || parts[0] !== 'lab') return null
  if (!parts[1] || !parts[2]) return null
  return { username: parts[1], role: parts[2] }
}
