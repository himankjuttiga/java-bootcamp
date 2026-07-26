# Lab 6 Answers

1. Streams let you write what you want in one readable pipeline instead of writing out loops and temporary variables by hand.
2. Streams are best when you are filtering, transforming, or aggregating a collection rather than doing complex step by step logic.
3. filter keeps or drops elements without changing their type, while map transforms each element into something else.
4. reduce is useful because it folds a whole stream down into a single value like a max, min, or total.
5. Collectors.groupingBy splits the stream into a map of buckets based on a key, like grouping employees by department.
6. Optional is helpful because it forces you to handle the empty case instead of getting a surprise NullPointerException.
7. Lambdas are more readable because they replace a big anonymous class with one short line that just says what to do.
8. Method references should be used when the lambda only calls one existing method, like Employee::getName.
9. Terminal operations run the pipeline, and three examples I used are forEach, count, and collect.
10. Streams make enterprise code cleaner and easier to maintain since reporting and filtering logic reads almost like plain English.
11. A future CRM would filter customers by status, map them to summaries, and group them by region the same way I grouped employees by department, just on customer data instead.