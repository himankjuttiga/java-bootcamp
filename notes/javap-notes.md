## Ex08 - Bytecode Inspection (Person)

- `new` — when I wrote `new Person("Aman", 21)` in Java, the JVM created space on the heap for that object
- `ldc` — the string `"Aman"` got loaded onto the stack so the constructor could use it
- `invokevirtual` — when I called `person.display()`, the JVM looked up and called that method on the object
