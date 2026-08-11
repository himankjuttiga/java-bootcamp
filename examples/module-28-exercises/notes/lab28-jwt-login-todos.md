# Lab 28 — JWT Login TODOs

## Login path + body

`POST /api/auth/login` with JSON body `{ "username": "agent1", "password": "..." }`. Open (`permitAll`).

## Token response

`{ "accessToken": "<jwt>", "tokenType": "Bearer" }`. `JwtService` handles `issueToken(user, role)`, `parseSubject(token)`, and `parseRole(token)` (lab stub acceptable).

## Bearer header form

Later protected calls send `Authorization: Bearer <accessToken>` — never the token in a query string.

## Lab users/roles

`agent1` -> role AGENT; `admin1` -> role ADMIN. Passwords stored bcrypt-encoded, never plaintext.

## Secret handling

Signing secret from environment: `JWT_SECRET` -> `northstar.security.jwt-secret`. `.env.example` carries a placeholder only; `.env` is gitignored. No real secret in notes or Git.

## Answers to the prompts

- **Password encoding vs token issue:** verify the submitted password against the stored bcrypt hash *first*; only after successful authentication does `JwtService` issue the token.
- **JWT in a query string:** no — query strings land in server logs, browser history, and referrer headers, leaking the token. Use the `Authorization` header.

## Scope

Pre-lab only. No real secrets.
