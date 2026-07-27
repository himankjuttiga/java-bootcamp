# Exception Hierarchy - Lab 7 ATM

## Custom checked exceptions (extend Exception)

| Exception | Rule it enforces | Thrown in | Caught in |
| --------- | ---------------- | --------- | --------- |
| InvalidAmountException | amount must be > 0 | Account.deposit / Account.withdraw | ATMService.executeTransaction |
| InsufficientFundsException | withdraw/transfer can't exceed balance (carries requested + available) | Account.withdraw | ATMService.executeTransaction |
| InvalidPinException | PIN must match, tracks attempts left (max 3) | ATMService.login / requireLogin | login or executeTransaction |
| AccountNotFoundException | account number must exist in the map | ATMService.findAccount | login or executeTransaction |

## Unchecked (handled in the demo, not custom)

- NullPointerException - calling a method on null
- ArithmeticException - divide by zero
- ArrayIndexOutOfBoundsException - bad array index

## Also handled at the boundary

- InputMismatchException - bad numeric input in readAmount
- IOException - reading transactions.txt / writing the log

## Flow

Account throws the domain exception -> ATMService catches it at the boundary (multi-catch in executeTransaction) -> LoggerUtil writes it to logs/application.log -> user sees a short message -> menu keeps running.