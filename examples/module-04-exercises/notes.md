# Module 4 Exercise Notes

## Exercise 1 — Stack vs Heap

A local variable holds a reference while the Person object itself lives on the heap, 
and primitives like count and nameLength sit directly in their own stack frames. 
Each method call gets its own frame, 
so main and printPerson can both reference the same heap object at once, 
and each frame is removed when its method returns.
## Exercise 2 — Object Lifecycle and Reachability
For object references == checks whether both point to the same object, 
so first == alias is true because no second Person was created. 
An object becomes GC-eligible only when no strong reference can reach it,
so first = null is not enough while alias still points to it, 
and System.gc() only requests collection rather than guaranteeing it.

## Exercise 3 — Garbage Collection in Action

Log excerpt from my run:
```
[info][gc] Using G1
[info][gc] GC(0) Pause Young (Normal) (G1 Evacuation Pause) 33M->15M
```

The program allocated about 250 MB over time but ran fine under a 64 MB max heap because each batch became unreachable on the next loop and the collector reclaimed it. 
"Allocated over time" is the total across all 20 rounds, while "simultaneously live" is only the one batch (~12.5 MB) held at any moment, 
which is why 262144000 bytes could pass through a much smaller heap.

## Exercise 4 — Select and Verify G1

Command:
java -XX:+UseG1GC -Xms16m -Xmx64m -Xlog:gc GcObserve

The log began with "Using G1" and showed G1 evacuation pauses, and the program still completed under the 64 MB heap. 
The flag only selects G1 as the collector; it does not guarantee a specific pause duration, 
since actual pause times depend on hardware, heap state, and the JVM's own ergonomics.

## Exercise 5 — Select and Verify ZGC

Command:
java -XX:+UseZGC -Xms16m -Xmx64m -Xlog:gc GcObserve

The log began with "Using The Z Garbage Collector" instead of "Using G1", and the program still completed under the 64 MB heap.
The log shape differs because ZGC does almost all of its work concurrently, 
so it does not report the stop-the-world "Evacuation Pause" lines that G1 shows.

## Exercise 6 — Retained References

Retaining path:
loaded RetentionDemo class -> static CACHE field -> ArrayList entries -> byte[] objects

Root cause: a long-lived static collection held strong references to the arrays even after they were no longer needed, 
so GC could not reclaim them because they stayed reachable through a GC root. 
Once CACHE.clear() removed those references the arrays became unreachable and therefore GC-eligible. 
A fix is to bound the cache, 
evict old entries, or clear it when done, rather than letting a static collection grow forever.


## Exercise 7 — String vs StringBuilder

| Run | String ms | StringBuilder ms |
| --- | --------- | ---------------- |
| 1 |  |  |
| 2 |  |  |
| 3 |  |  |

Across all three runs StringBuilder was far faster. String is immutable, 
so result += "x" builds a brand new String and copies every existing character each pass,
creating 50,000 throwaway objects. 
StringBuilder keeps one mutable buffer and appends in place, 
so it does almost no copying and produces little garbage. 
Use StringBuilder when building text in a loop; plain + is fine for a small, 
fixed expression.