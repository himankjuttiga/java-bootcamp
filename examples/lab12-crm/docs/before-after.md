# Before / After — CustomerService Refactor (Lab 12)

## Line count
| File | Lines |
| ---- | ----- |
| `CustomerService.before.java.txt` (frozen mess) | 58 |
| `CustomerService.java` (refactored) | 66 |

Length is not the win; responsibility is. The before file packed create, update, status
mapping and logging into one `doStuff`. The after file spends its lines on named methods and
validation helpers that each do one thing.

## Smell → fix mapping (see docs/smells.md)
| # | Smell (before) | Fix (after) |
| - | -------------- | ----------- |
| 1 | `doStuff`, `data`, params `a b c d e` | `createCustomer/getCustomer/updateStatus`, `customersById`, named params |
| 2 | `List data` raw type | `Map<String, Customer> customersById` |
| 3 | One method does validate + create + update | Split methods + `requireNonBlank/requireUniqueId/requireExisting` |
| 4 | `e.equals("ACTIVE")` string chains | Typed `CustomerStatus` on the API |
| 5 | `getCustomerId() == id` (reference equality) | `Map.get(id)` value-keyed lookup |
| 6 | `return null` on errors | `IllegalArgumentException` / `IllegalStateException` |
| 7 | `System.out.println("bad"/"dup"/...)` | Correlation ID in exception messages |
| 8 | Name containing `"UPDATE"` triggers update | Removed; status changes only via `updateStatus` |
| 9 | Duplicated status-mapping logic | Single typed path, no duplication |
| 10 | `Object` return type | Returns `Customer` |

## Methods: before vs after
| Before | After |
| ------ | ----- |
| `Object doStuff(String,String,String,String,String)` | `Customer createCustomer(String, String, String, String, CustomerStatus)` |
| `Object get(String)` | `Customer getCustomer(String)` |
| (update jammed inside doStuff) | `Customer updateStatus(String, CustomerStatus)` |
| — | private `requireNonBlank`, `requireUniqueId`, `requireExisting` |

## Test result
`mvn clean test` → BUILD SUCCESS · **Tests run: 8, Failures: 0, Errors: 0**
(`CustomerTest` 2 + `CustomerServiceTest` 6). The `CustomerServiceTest` cases were written
red first against the target API, then went green after Steps 4–5.

## Manual demo transcript (Main)
```text
Get CUS-1001: Amina Khan
After activation CUS-1002: ACTIVE
Duplicate rejected: Customer already exists: CUS-1001 correlationId=lab-request-001
Unknown rejected: Customer not found: CUS-9999 correlationId=lab-request-001
```
Shows: create + get for CUS-1001, PROSPECT→ACTIVE for CUS-1002, and both failure paths
throwing clearly with `correlationId=lab-request-001`.