# Lab 5 Answers — Java Collections Framework (Library Management System)

## Collection Mapping (which field uses what and why)

| Field | Collection | Why |
| ----- | ---------- | --- |
| `books` | `ArrayList<Book>` | Ordered catalog with fast random access and iteration. |
| `members` | `ArrayList<Member>` | Ordered roster, same access needs as books. |
| `bookIds` / `memberIds` | `HashSet<String>` | O(1) duplicate-ID detection before inserts. |
| `borrowRecords` | `HashMap<String,String>` | Fast book to current-borrower lookup. |
| `categories` | `TreeSet<String>` | Unique category names kept in sorted order. |
| `categoryBookCount` | `TreeMap<String,Integer>` | Sorted counts for clean category reporting. |
| `borrowHistory` | `ArrayList<BorrowRecord>` | Ordered audit trail of borrow events over time. |

---

## Reflection Questions

1. **When choose `List` over `Set`?**
   Use a `List` when order matters or duplicates are allowed; use a `Set` only when every value must be unique.

2. **Why `HashSet` before inserting a book ID?**
   It gives O(1) duplicate detection, so rejecting an existing ID is fast and does not require scanning the whole list.

3. **Why a `Map` for "currently borrowed" vs only a boolean?**
   A boolean only says if a book is out; a `Map` also records who has it, answering the borrower question a flag cannot.

4. **`HashMap` vs `TreeMap` in this lab?**
   `HashMap` gives fast unordered lookups for borrow records; `TreeMap` keeps category counts sorted for readable reports.

5. **`Comparable` vs `Comparator` for books?**
   `Comparable` defines the book's natural order (by title); `Comparator` (`BookComparator`) adds an alternate order like price without changing the class.

6. **Which iteration style would you use most in production, and why?**
   Enhanced `for` or `forEach`, because they are concise, readable, and avoid manual index or iterator errors.

7. **CRM: which collection for customer list / unique emails / id to customer lookup?**
   `ArrayList` for the customer list, `HashSet` for unique emails, and `HashMap` for id to customer lookup.

---

## Performance Table (ArrayList vs LinkedList)

> Replace with the numbers from your own run; values below are illustrative.

| Operation | ArrayList | LinkedList |
| --------- | --------- | ---------- |
| Insert N at end | ~5 ms | ~9 ms |
| Random access (get) | ~1 ms | ~40 ms |

---

## LMS Write-up

**Compile:**
`javac -d out src\com\academy\library\*.java` (or name each file on Windows PowerShell)

**Run:**
`java -cp out com.academy.library.Main`

**Summary:** All library data lives in Java Collections for the life of the process. Lists hold ordered book and member data, Sets guard unique IDs, a Map tracks current borrowers, and TreeSet/TreeMap drive sorted category reporting.