# AI Review Notes - Lab 10 (Northstar CRM)

## lab10-001 - Weak vs strong prompt (Customer entity)
- Weak: "customer class." Copilot guessed fields and risked JPA annotations.
- Strong: named every field, type, format (CUS-1001), and "plain Java 21, no Spring, no JPA, no Lombok."
- Output: a clean POJO with the exact fields, constructors, getters/setters, equals/hashCode on customerId, toString.
- Decision: partial - accepted the shape, but had to fix it (see modified below).
- Reason: the strong prompt produced usable code; the weak one would have invented structure.

## lab10-002 - Weak vs strong prompt (addCustomer)
- Weak: "add a customer." Only a happy path, no validation.
- Strong: spelled out reject-blank-id, reject-duplicate (IllegalStateException), else store and return.
- Output: guard clauses matched the rules.
- Decision: accept.
- Reason: stating rules up front produced the validation instead of just an add.

## lab10-003 - Human review checklist
| # | Check | Result |
| - | ----- | ------ |
| 1 | Every import resolves against pom.xml (no phantom JPA/Spring) | Pass |
| 2 | Business rules in code, not just comments (blank/duplicate/unknown id) | Pass |
| 3 | equals/hashCode based on customerId only | Pass |
| 4 | I can explain every line with Copilot off | Pass |
| 5 | No secrets or real PII committed | Pass |

## Accepted / Modified / Rejected

- ACCEPTED: CustomerService. Generated clean with correct guard clauses and no Spring annotations. Validated by running Main (CUS-1001/CUS-1002, status filter, updateStatus).
- MODIFIED: Customer entity. The first generation left a stray "=======" git-style conflict marker and stopped before finishing toString. I removed the marker and completed the class. Validated by mvn compile and Main output.
- REJECTED: When asked to "add a save method to Customer," Copilot invented a persistence-style method assuming a database/@Entity. Rejected it, because this project has no JPA and Customer is a plain POJO. Recorded as failure experiment 1.

## lab10-004 - AI risk notes
1. Real customer data avoided: used only fixtures CUS-1001 / CUS-1002 and example.com emails, never real PII or secrets in Chat.
2. If a suggestion looks copied verbatim from a known library or article, I stop, check its size and license, and rewrite it in my own style rather than accept a possibly copyrighted block.
3. Team rule: any code I do not fully understand does not get accepted. I either rewrite it myself or reject it, since I am responsible for it, not Copilot.

## Failure experiments
1. Asked Chat to add a save method to Customer with no context - it invented a DB/@Entity-style method. Rejected.
2. Disabled Copilot and added a deleteCustomer(String) by hand - finished fine without AI, just slower.
3. Drafted (did not send) a prompt with a fake SSN as an example - unsafe even when fake, rewrote using only CUS-1001/CUS-1002.
4. Asked Chat to build the entire CRM service layer in one shot - oversized, hard to review. Preferred scoped prompts.

## Concepts (brief)
- Inline completion is best for finishing a line or a method you have started; Chat is better for a whole multi-method class.
- Copilot is a design-time tool only; it is never a runtime dependency of customer-service.
- The trust boundary: no AI suggestion touches real customer data until a human reviews and validates it.