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