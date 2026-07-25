# Lab 4 Answers — Memory Management and Garbage Collection

## Reflection Questions

1. **Stack vs Heap?**
   The stack holds per-thread method frames with primitives and references; the heap holds the actual shared objects.

2. **Why locals on the Stack?**
   Locals have a short lifetime tied to their method call, so the stack can allocate and free them automatically as frames push and pop.

3. **Why objects on the Heap?**
   Objects can outlive the method that made them and be shared by many references, so they need the longer-lived shared heap.

4. **When is an object GC-eligible?**
   When no live thread can reach it through any chain of references (no strong references remain).

5. **Does `System.gc()` guarantee collection?**
   No. It is only a hint, and the JVM may delay or ignore it.

6. **What caused the leak?**
   Objects were added to a static (long-lived) list and never removed, so they stayed reachable and could not be collected.

7. **How did clearing the list fix it?**
   `clear()` plus nulling the reference dropped the strong references, making the objects unreachable and eligible for GC.

8. **Why are WeakReferences useful?**
   They let the GC reclaim an object once only weak references remain, which is ideal for caches that should not pin objects forever.

9. **What happens when the heap is exhausted?**
   The JVM throws `OutOfMemoryError: Java heap space` when it cannot allocate and GC cannot free enough room.

10. **Which laptop tool would you try first for rising heap, and why?**
    `jstat`, because it needs no GUI and quickly shows heap usage and GC activity per interval from the command line.

11. **How could a CRM unbounded cache repeat this leak?**
    A cache that keeps every customer object forever holds strong references indefinitely, so memory climbs exactly like the static-list leak.

---

## Performance Table

> Replace with the numbers from your own run; values below are illustrative.

| Objects | Used Memory (approx) | Execution Time |
| ------- | -------------------- | -------------- |
| 10 | ~2 MB | ~0 ms |
| 100 | ~2 MB | ~0 ms |
| 1,000 | ~3 MB | ~1 ms |
| 100,000 | ~12 MB | ~15 ms |
| 1,000,000 | ~95 MB | ~120 ms |

---

## Leak vs Fix (one paragraph)

In `leak` mode, `Employee` objects are added to a static list that is never cleared, so every object stays reachable and used memory keeps climbing because GC cannot reclaim referenced objects. In `fix` mode, the objects go into a local list that is cleared and nulled before triggering GC, which removes all strong references and lets the collector free the memory, so used memory drops after GC.

---

## LMS Overview

**Tools used:** `Runtime` memory reports via a shared `MemoryMonitor`, GC logging with `-Xlog:gc`, and optionally `jstat` for live heap counters.

**Leak cause and fix:** The leak came from retaining objects in a static list that was never cleared, keeping them reachable. Clearing the list, nulling the reference, and running GC removed the strong references and recovered the memory.