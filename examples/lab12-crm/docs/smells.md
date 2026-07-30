# Code Smells — CustomerService.before.java.txt (Lab 12)

Baseline: `CustomerService.before.java.txt` (~62 lines). Each smell below cites where
it lives in the frozen file and the concrete impact on support handling CUS-1001 / CUS-1002.

| # | Smell | Where in baseline | CRM impact |
| - | ----- | ----------------- | ---------- |
| 1 | Unclear naming | `doStuff(...)`, `data`, params `a b c d e` | Support and reviewers cannot tell what the method does. Nobody can say why creating CUS-1001 also has an update path hidden inside `doStuff`. |
| 2 | Raw types | `List data = new ArrayList();` | No compile-time type safety. A non-Customer could be added and only blow up at the `(Customer)` cast when someone looks up Ravi. |
| 3 | Long method / mixed responsibilities | `doStuff` does validate + create + status-map + update in one body | One method owns four jobs, so a change to the update rule risks breaking create for CUS-1001. Impossible to test in isolation. |
| 4 | Stringly-typed status | `e.equals("ACTIVE")` / `"PROSPECT"` / `"SUSPENDED"` / `"CLOSED"` chains | A caller passing `"active"` or a typo silently falls through to the `else` and Ravi is created as PROSPECT instead of his real status. No compiler check. |
| 5 | Incorrect equality on Strings | `get`: `x.getCustomerId() == id` | Looks up by reference, not value. `getCustomer(new String("CUS-1001"))` returns null even though Amina exists. This is the real "support cannot find Amina" bug. |
| 6 | Null as control flow | `return null` on blank input, on duplicate, and on not-found in `get` | Callers get null for three different failure meanings and cannot distinguish them. Leads to NullPointerException downstream instead of a clear error. |
| 7 | Side-effect logging | `System.out.println("bad" / "dup" / "ok" / "upd")` | Logging is mixed into business rules with no correlation ID. Support cannot trace lab-request-001 or tell a duplicate from a bad input in the output. |
| 8 | Magic behavior | `if (b != null && b.contains("UPDATE"))` triggers a status update | A customer whose name contains "UPDATE" silently mutates status. A create for a customer named "UPDATE Corp" would behave unpredictably. Undocumented and support-hostile. |
| 9 | Duplicated status-mapping logic | The `e.equals(...)` chain appears in both the create block and the `"UPDATE"` block | Same mapping written twice, so a fix in one place is easily forgotten in the other, producing inconsistent status handling for CUS-1002. |
| 10 | Weak return type | `public Object doStuff(...)` and `public Object get(...)` | Returning `Object` forces every caller to cast, and hides that the method returns a Customer. Defeats type safety and readability for Lab 13 callers. |

## Priority (fix first)
1. Smell 5 (`==` on IDs) — the production-class lookup bug.
2. Smell 6 (null as control flow) — replace with exceptions so failures are legible.