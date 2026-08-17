import { http } from './http'

interface LoginResponse {
  accessToken: string
  tokenType: string
}

export const authApi = {
  /**
   * POST /api/auth/login -> { accessToken, tokenType }.
   * The token goes straight to the in-memory store: never logged, never persisted.
   */
  login(username: string, password: string): Promise<LoginResponse> {
    return http<LoginResponse>('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    })
  },
}
