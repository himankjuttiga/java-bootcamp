# Lab 20 — MDC Lifecycle

## Put

On request entry, put the correlation ID into the MDC: `MDC.put("corr", "lab-request-001")`. The ID is validated from the incoming `X-Correlation-Id` header, or generated if absent, before it is stored.

## Use

Every log line emitted during that request automatically includes the value via the Logback pattern `%X{corr}`  no need to pass the ID into each log call. Service-layer logs (e.g. `event=customer.activation customerId=CUS-1002`) pick it up implicitly.

## Clear

Always clear in a finally block: `finally { MDC.clear(); }`. This runs on every request, including exceptions, so the pooled thread never carries the ID into the next request.

## Lab 21 boundary

Correlation IDs in MDC are application-level request scoping only. Full observability: metrics, Actuator endpoints, and real distributed tracing (trace ID, span ID, spans)  is deferred to Lab 21.

## Debug / design challenge

Put should happen in a **filter/interceptor that wraps all requests**, not in individual controller method bodies. A filter guarantees the ID is set before any downstream code runs and, paired with its finally block, guarantees the clear on every exit path. Per-controller puts are easy to forget, duplicate, and leak.

## Predict the output / behavior

If you never call `MDC.put`, the `%X{corr}` pattern still renders, it just resolves to empty (or the default you specify, e.g. `%X{corr:-none}`). It does not error; the correlation field is simply blank, which is exactly why the filter must set it on entry.

## Scope

Pre-lab only.