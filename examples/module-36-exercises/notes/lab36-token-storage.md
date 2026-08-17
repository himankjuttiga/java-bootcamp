# Lab 36 — Token Storage Options

## Reference

| Option | Risk / note |
| --- | --- |
| In-memory variable | Lost on refresh; safer from XSS persistence |
| sessionStorage | Per-tab; XSS can read |
| localStorage | Survives refresh; XSS can read |
| HttpOnly cookie | Not JS-readable; needs CSRF strategy |

## Step 1 — Study table

Copied above. The distinction that matters is not "can XSS read it" for a single page view, since
script running in our origin can read anything the page can reach. It is **persistence**: a token in
`localStorage` is still there tomorrow, so one XSS hit yields a token that keeps working long after
the malicious script is gone. A token in a module variable dies with the tab.

## Step 2 — Lab choice

**In-memory only**, matching the starter's `tokenStore` module variable. An access token that lives in
a closure is never serialised anywhere an attacker's script can quietly harvest at leisure, and it
disappears on refresh, which bounds the blast radius of any single XSS to that page view.

The cost is honest and accepted: refreshing the tab logs the user out, because there is nothing to
rehydrate from. In production the fix is a short-lived access token in memory refreshed by an
`HttpOnly` `SameSite` cookie, not a longer-lived token parked in Web Storage. Putting a refresh token
in `localStorage` "for convenience" trades a one page-view compromise for a persistent one, which is
the worst possible bargain: it hands an attacker the ability to mint new access tokens indefinitely.

## Step 3 — Never

* Never commit a token, real or expired, to git. Markdown notes are committed too.
* Never put DB passwords, Kafka credentials or signing keys in `VITE_*`. Those values are compiled
  into the browser bundle.
* Never log a full token. Log the correlation id `lab-request-001` instead, so a support ticket is
  traceable without being a credential.
* Never write the access token to `localStorage` or `sessionStorage` in this lab.

## Step 4 — Fixture

Fake token used in notes and stubs: `lab-token-001`. It is a placeholder string, not a JWT, and it
authenticates nothing.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
