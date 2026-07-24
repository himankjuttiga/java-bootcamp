# Collection choices

| # | Scenario | Need (order / unique / key→value / sorted) | Interface | Implementation | Why |
| - | -------- | ------------------------------------------ | --------- | -------------- | --- |
| 1 | Ordered catalog; duplicate titles allowed | order, duplicates | `List<Book>` | `ArrayList<>` | Indexed sequence that keeps insertion order and allows duplicates |
| 2 | Unique registered book IDs | unique | `Set<String>` | `HashSet<>` | Rejects duplicates and gives fast membership checks |
| 3 | Book ID → current borrower ID | key→value | `Map<String, String>` | `HashMap<>` | Direct lookup of a borrower by book ID |
| 4 | Alphabetically sorted categories | unique, sorted | `Set<String>` | `TreeSet<>` | Unique values iterated in natural sorted order |
| 5 | Category → count, sorted by category | key→value, sorted | `Map<String, Integer>` | `TreeMap<>` | Key to value mapping with keys kept in sorted order |
| 6 | Checkout history in event order | order | `List<BorrowRecord>` | `ArrayList<>` | Append and iterate in insertion order |

## Ambiguous requirements

1. If unique IDs must also preserve registration order, switch the Set to `LinkedHashSet`, which keeps uniqueness but iterates in insertion order.
2. If borrower lookup must preserve insertion order for display, switch the Map to `LinkedHashMap`, which keeps key to value lookup but iterates in insertion order.
3. `LinkedList` is not automatically best for many middle insertions. Access pattern and traversal cost matter, and `ArrayList` often wins in practice, so measure rather than assume.