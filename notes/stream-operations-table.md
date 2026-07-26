# Stream Operations Table

| Operation / API | Used? | Where (method / menu) | Notes |
| --------------- | :---: | --------------------- | ----- |
| Lambda forEach | Yes | displayAllEmployees / menu 1 | prints each employee |
| Predicate | Yes | demonstrateFunctionalInterfaces / menu 11 | tests salary over 100k |
| Function | Yes | demonstrateFunctionalInterfaces / menu 11 | maps to a summary string |
| Consumer | Yes | demonstrateFunctionalInterfaces / menu 11 | prints a rating |
| Supplier | Yes | demonstrateFunctionalInterfaces / menu 11 | supplies highest paid |
| filter | Yes | displayHighSalaryEmployees / menu 13 | keeps salary over 80k |
| map | Yes | demonstrateMapping / menu 15 | projects to name or salary |
| sorted | Yes | demonstrateSorting / menu 16 | orders by salary and name |
| distinct | Yes | displayDistinctDepartments / menu 17 | unique departments |
| limit / skip | Yes | displayTopAndNextSalaries / menu 18 | top 5 and next 5 |
| count | Yes | displayCounts / menu 19 | headcount queries |
| reduce | Yes | displayReductions / menu 3 | max and min salary |
| collect(toList/toSet) | Yes | demonstrateCollectors / menu 20 | active list and dept set |
| groupingBy | Yes | displayGroupedEmployees / menu 2 | employees by department |
| partitioningBy | Yes | displayPartitionedEmployees / menu 3 | above or below 100k |
| summarizingDouble | Yes | displaySummaryStatistics / menu 3 | salary stats in one pass |
| Optional (max / ifPresent) | Yes | displayHighestPaidEmployeeOptional / menu 5 | safe highest paid lookup |
| Method references | Yes | throughout | Employee::getName, System.out::println |
| Dashboard composed report | Yes | displayDashboard / menu 8 | full executive summary |