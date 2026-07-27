# Lab 7 - Exception Handling and Error Management (ATM Banking System)

My answers for the Module 7 lab. Code is under `examples/Lab7-ATMSystem/src/com/academy/atm/`.

## Concepts

1. **Why are InvalidAmountException and friends checked, but NullPointerException unchecked?**
   The custom ones are business rules (bad amount, no funds, wrong PIN, missing account) that a caller can actually recover from, so I want the compiler to force me to handle them. NPE is a programming bug, not something the user should have to catch every time.

2. **What does `throws` on `Account.withdraw` force callers to do?**
   The caller (ATMService) has to either catch those exceptions or declare them too. It can't just ignore them.

3. **Why catch specific exceptions before a broad `catch (Exception)`?**
   So each error gets its own message and log. Java also won't compile if the broad catch comes first, because the specific ones become unreachable.

4. **What does `finally` guarantee that `catch` alone doesn't?**
   `finally` runs on both the success and failure paths. That's how the "Returning to Main Menu" message always prints no matter what happened.

5. **Why prefer try-with-resources over `reader.close()` in a `finally`?**
   It closes the reader automatically even if an exception is thrown, so I can't forget to close it and leak the file.

6. **Why log stack traces to a file but show short messages to the user?**
   The user just needs to know it failed. The full stack trace is for me/ops to debug, and dumping it on screen looks bad and can leak internal detail.

7. **Where should validation throw, deep in Account or only in Main?**
   Deep in Account, because that's where the balance actually changes. If validation only lived in Main, another caller could skip it and corrupt the balance.

8. **How will a future CRM reuse this pattern?**
   Same idea: the service throws a domain exception (not found, validation failed), a boundary catches it, logs it, and turns it into an API error response instead of crashing the server. Not built today.

## Reflection Questions

1. **Checked vs unchecked?**
   Checked exceptions must be caught or declared and are checked at compile time (IOException, my custom ones). Unchecked extend RuntimeException and usually mean a bug (NPE, ArithmeticException).

2. **Why use custom exceptions?**
   They give the error a clear name and can carry extra info, like the requested amount and balance. Catching `InsufficientFundsException` is way clearer than reading a generic message.

3. **What is exception propagation?**
   When a method doesn't handle an exception it passes up the call stack to whoever called it, until something catches it. Here it goes Account -> ATMService -> Main.

4. **Purpose of finally?**
   To run cleanup or a final action whether or not an exception happened.

5. **Why is try-with-resources preferred?**
   It auto-closes resources and preserves any suppressed exceptions, so no manual close in a finally block.

6. **When to use `throw`?**
   Inside a method to actually raise an exception when a rule is broken, like amount <= 0.

7. **When to use `throws`?**
   On a method signature to say it might pass a checked exception up to the caller.

8. **Why is logging important in enterprise apps?**
   When something breaks in production you need a record of what happened, when, and the stack trace, otherwise you're guessing.

9. **What happens if an exception isn't handled?**
   It keeps propagating up, and if nothing catches it the program crashes and prints a stack trace.

10. **How does proper exception handling improve reliability?**
    The app keeps running through bad input and business errors instead of dying, users get clear messages, and errors are logged for fixing.

11. **How would CRM map domain exceptions to API errors?**
    A service throws something like NotFound or ValidationFailed, a boundary handler catches it, logs it, and returns the right HTTP status and message. Same catch-at-the-boundary and log habit, just applied to an API. (Not implemented here.)

## Checkpoints

### Checkpoint A - Project + exceptions + model
1. Package folder exists - Pass
2. Four custom exceptions + Account + transactions.txt + logs/ present - Pass
3. Seed accounts 1001/1234/$11000 and 1002/5678/$5000 - Pass
4. Edited in IntelliJ - Pass

### Checkpoint B - Service + Main compile
1. ATMService, LoggerUtil, Transaction, Main present - Pass
2. javac -d out compiles - Pass
3. java -cp out runs and shows menu from project root - Pass
4. Exit prints Thank You and terminates - Pass

### Checkpoint C - Exception behavior
1. Withdraw more than balance -> Insufficient Balance, menu continues - Pass
2. Invalid amount / bad PIN / missing account give ERROR, not crashes - Pass
3. Invalid numeric input shows the messages and continues - Pass
4. finally prints return-to-menu text - Pass
5. try-with-resources handles missing transactions.txt - Pass

### Checkpoint D - Logging + evidence
1. logs/application.log has ERROR (and INFO) entries - Pass
2. Exception hierarchy notes filled, reflection done - Pass
3. Screenshots of success and failure paths saved - Pass