# Lab 20 — Clear MDC Finally Drill

## Bug story

Request A sets `MDC.put("corr", "lab-request-001")` on pooled thread T7 but never clears it. T7 returns to the pool still holding the value. Request B reuses T7 and its logs render `corr=lab-request-001` — B's entries are wrongly attributed to A.

## Fix

Set the ID in a request filter and clear it in `finally { MDC.clear(); }`, so it runs on every exit path including exceptions.

## Test idea

Drive request A (`corr-A`) then B (`corr-B`); assert no `corr-A` leaks into B, and that MDC is empty after each request.

## Debug / design challenge

The `finally` block must clear MDC — it is the only path that runs even when an exception skips the return.

## Predict the output / behavior

A `static String` is shared across threads (race conditions), is never request-scoped or cleared, and does not feed `%X{}`. MDC is thread-local and clearable, which is what request scoping needs.

## Scope

Pre-lab only.