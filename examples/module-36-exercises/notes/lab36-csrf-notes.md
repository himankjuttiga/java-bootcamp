# Lab 36 — CSRF Notes

## Step 1 — Cookie sessions

CSRF exists because the browser attaches cookies to a request automatically, based on the
destination, without the sending page needing to know anything. If CRM auth were a session cookie,
then `https://evil.example` could post a form to `http://localhost:8080/api/customers` and the
browser would helpfully include our session, executing a write as the logged-in agent. The attacker
never reads the response; the damage is the side effect.

Defences in that model: `SameSite=Lax` or `Strict` on the auth cookie, a synchroniser token or
double-submit cookie for state-changing requests, and never treating a plain 200 as proof of intent.

## Step 2 — Bearer header

If the token lives in JavaScript memory and is attached explicitly as `Authorization: Bearer …`, a
cross-site request carries no credential at all. The attacker's page cannot read our token, because
it is in another origin's memory, and the browser will not attach it for them. Classic CSRF is
therefore reduced to near zero, which is why Spring can keep `csrf.disable()` on a stateless bearer
API without that being negligent.

The tradeoff moves rather than vanishes: bearer tokens shift the risk to XSS, since any script in
our origin can read the token and use it directly. Cookie plus `HttpOnly` protects against reading
but reintroduces CSRF. There is no option with neither risk, only a choice about which one you are
equipped to control.

## Step 3 — Lab stance

The Lab 36 starter is **bearer only**. `src/auth/tokenStore.ts` holds the token in a module variable,
and `src/api/http.ts` sets `Authorization: Bearer <token>` explicitly, only when the request URL
starts with the configured API base. Nothing is stored in cookies, so no cookie is auto-attached and
classic CSRF is out of scope for this lab.

The origin check matters as much as the header: attaching the token to any URL a component happens to
pass would leak the credential to a third-party host the moment someone fetches an avatar or an
analytics endpoint through the same helper. Bearer tokens must be scoped to the CRM API origin.

CSRF returns to scope the moment the design switches to cookie sessions, so the decision belongs in
`docs/security-decisions.md` rather than in a developer's head.

## Step 4 — Checklist

* [ ] If cookies are ever used for auth, set `SameSite=Lax` at minimum, `Strict` where flows allow.
* [ ] Pair cookie auth with `HttpOnly` and `Secure`, and add a CSRF token for state-changing requests.
* [ ] While bearer only, keep `Authorization` off any non-CRM origin, enforced in `http.ts`.
* [ ] Keep the CSRF stance written down in `docs/security-decisions.md`, including why it is N/A today.
* [ ] Re-open this decision if a future lab adds refresh cookies or a third-party identity provider.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
