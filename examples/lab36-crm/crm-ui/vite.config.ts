import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  /**
   * Lab 36 — browser security headers on the SPA host.
   *
   * The dev policy has to allow 'unsafe-inline' and 'unsafe-eval' because Vite's HMR client
   * injects inline scripts and evaluates modules. Production serves a built bundle and tightens
   * script-src to 'self' with hashes or a nonce; that difference is documented in
   * docs/security-decisions.md rather than hidden here.
   */
  server: {
    headers: {
      'Content-Security-Policy': [
        "default-src 'self'",
        "script-src 'self' 'unsafe-inline' 'unsafe-eval'",
        "style-src 'self' 'unsafe-inline'",
        "connect-src 'self' http://localhost:8080 ws://localhost:5173",
        "object-src 'none'",
        "frame-ancestors 'none'",
        "base-uri 'self'",
      ].join('; '),
      'X-Content-Type-Options': 'nosniff',
      'Referrer-Policy': 'no-referrer',
      'X-Frame-Options': 'DENY',
    },
  },
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: './src/test/setup.ts',
  },
})
