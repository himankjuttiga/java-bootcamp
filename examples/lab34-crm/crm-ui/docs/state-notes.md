# Lab 34 — State notes

## Lifted state

`mode`, `draft`, `errors`, and `customers` all live in `App`, not in a
per-row component, because create and edit share one exclusive `UiMode`
and one `draft`/`errors` pair. If each row owned its own "am I editing"
boolean, two rows could enter edit mode at once, or a row could hold a
stale draft after the list re-renders elsewhere. Lifting state to the
single closest common ancestor (`App`) keeps "only one thing can be
edited at a time" enforceable by the type system — the `UiMode`
discriminated union — instead of relying on convention.

## Validation

`validateCustomerDraft` is client-side UX only — it lets a user fix an
empty name or a malformed email before a round trip, but it is not a
security boundary. Lab 35 wires this form to a real Spring Boot
endpoint, and the server re-validates every field independently; the
client check must never be treated as the source of truth for what's
actually allowed to be saved.
