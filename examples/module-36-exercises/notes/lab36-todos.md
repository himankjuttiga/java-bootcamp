# Lab 36 — Fill Route Guard TODOs

## Step 1 — Paste

```tsx
function RequireAuth({ children }: { children: React.ReactNode }) {
  const token = getAccessToken(); // tokenStore.get(), in-memory only
  if (!token) {
    return <Navigate to="/login" replace />;
  }
  return <>{children}</>;
}

// TODO: attach Authorization: Bearer lab-token-001 on API fetch (Lab 35+36),
//       and only when the URL starts with VITE_API_BASE_URL.
// TODO: never log full token; log correlation lab-request-001 instead
```

## Step 2 — Fill

| Blank | Value | Why |
| --- | --- | --- |
| `token` | `getAccessToken()` wrapping `tokenStore.get()` | memory only, no Web Storage read |
| redirect target | `"/login"` | relative in-app path, never a value from the URL, which would be an open redirect |
| return value | `<>{children}</>` | render the guarded tree unchanged once authenticated |
| header value | `lab-token-001` | fake placeholder; a real JWT must never appear in notes |

`replace` matters: without it the guarded URL stays in history, so Back bounces the user between the
login page and the guard.

## Step 3 — Role note

```tsx
// TODO: hide AdminMenu unless role === 'ADMIN'  (UI only, not authorization)
```

Hiding the menu is tidiness, not a control. The `/api/admin/**` endpoints are gated by
`hasRole("ADMIN")` in Spring Security, and that check is what actually stops an AGENT who edits the
role value in devtools and re-renders the menu for themselves.

## Step 4 — Backend reminder

Spring Security must reject unauthorized API calls for customer data regardless of what the SPA
renders. Lab 35 temporarily ran the API under a `lab35` profile that permitted `/api/customers/**`
because the SPA had no login screen yet. Lab 36 restores
`.requestMatchers("/api/customers/**").hasAnyRole("AGENT","ADMIN")`, and the proof is a curl call with
no token returning 401 rather than a customer list.

Two response codes, two meanings, to keep distinct in the UI:

| Code | Meaning | UI reaction |
| --- | --- | --- |
| 401 | no valid token, or it expired | clear the token, send the user to login |
| 403 | valid token, insufficient role | keep the session, show "you do not have access" |

Treating 403 as a logout trains users to re-authenticate against a wall that will never open.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
