# Lab 28 readiness checklist

| File | Present? (yes/no) |
| ---- | ----------------- |
| notes/authn-authz.md | yes |
| notes/filter-chain.md | yes |
| notes/lab28-jwt-login-todos.md | yes |
| notes/mockmvc-matrix.md | yes |
| notes/security-notes-outline.md | yes |

## Scope

Pre-lab only. Real JWT secrets in Git? no — the signing secret is an `${JWT_SECRET}` / `northstar.security.jwt-secret` env reference, `.env` is gitignored, and only `.env.example` placeholders are committed. 401 vs 403 clear: 401 = not authenticated (missing/bad/expired token), 403 = authenticated but wrong role. No OAuth2 authorization server stood up; lab JWT is teaching mode.

## Self mark

Overall prep: Pass

If Fail, revisit: whichever exercise owns the missing or incorrect artifact.

## Answers to the prompts

- **If authn-authz still swaps 401/403:** reopen Exercise 1 and fix the status mapping.
- **Is a React login UI required for the timed-path Pass?** No — the Pass is proven by the MockMvc 401/403/200 matrix and the login endpoint, not a front-end.
