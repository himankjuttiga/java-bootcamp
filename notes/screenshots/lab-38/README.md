# Lab 38 evidence — screenshots to capture

Run the scripts in order against the Lab 37 container. Each capture is a plan or a proof, so
frame the `QUERY PLAN` block including the `Buffers:` and `Execution Time:` lines. Redact nothing
here except any prompt that shows a password.

| # | File | What it must show |
| - | ---- | ----------------- |
| 1 | `01-volume-and-skew.png` | End of `01_generate_data.sql`: 50,002 customers, ACTIVE 70.0% / PROSPECT 30.0%, and the two fixture rows CUS-1001 and CUS-1002 still present |
| 2 | `02-stats-gathered.png` | `02_baseline.sql` statistics block: `approx_rows` and `last_analyze` for `customer` and `account` |
| 3 | `03-baseline-email.png` | lab38-001: `Seq Scan on customer`, `Rows Removed by Filter: 50001`, `Buffers: shared hit=782` |
| 4 | `04-after-email-index.png` | lab38-002: `Index Scan using ux_customer_email`, `Index Cond`, buffers in single digits |
| 5 | `05-list-before-after.png` | lab38-003 and lab38-004 side by side: Seq Scan plus Sort, then Index Scan with no Sort node |
| 6 | `06-sargability.png` | lab38-009 and lab38-010 plus the equivalence row showing 556 / 556 and zero differences either way |
| 7 | `07-joins.png` | lab38-011 Nested Loop for one customer, lab38-012 Hash Join for all ACTIVE, with the row counts that explain each |
| 8 | `08-offset-vs-keyset.png` | lab38-006 deep OFFSET at 5,047 buffers next to lab38-014 keyset at 23 buffers |
| 9 | `09-paging-correctness.png` | the deterministic OFFSET check with `overlapping_ids 0`, and the keyset walk with 60 distinct ids across three pages |
| 10 | `10-index-challenge.png` | `05_cleanup_indexes.sql`: at least one drop-and-remeasure regression, the write-cost insert, and the retained index sizes table |

Item 8 is the one to lead with if you are asked to show a single result. It is the only place where
elapsed time and buffer count disagree about whether a change was an improvement.
