# Lab 14 — Fill ValidatorFactory TODOs

## Step 1 — Copy TODOs

Bootstrap: `ValidatorFactory factory = Validation.buildDefaultValidatorFactory();`
`Validator validator = factory.getValidator();`
Invalid blank name → expect `1` violation ("fullName must not be blank")
Invalid status TYPO → expect `1` violation (value not in the allowed status set)
Valid Amina ACTIVE sketch → expect `0` violations
Spring @Valid in this pre-lab? `no` — plain Jakarta Bean Validation only, no Spring MVC

## Step 2 — Fill blanks

The factory is bootstrapped with `Validation.buildDefaultValidatorFactory()`, and the
`Validator` comes from `factory.getValidator()`. Running `validator.validate(dto)` returns a
`Set<ConstraintViolation<T>>`; the count and messages are what we assert. Amina's valid DTO
yields an empty set (0 violations); each bad field yields one violation. No Spring `@Valid`
is used here, that arrives with Spring MVC in the full lab.

## Step 3 — Invalid cases list

- Blank `fullName` on create → 1 violation (must not be blank)
- Unknown `status` (e.g. "GOLD"/typo) → 1 violation (not an allowed status)
- Null `customerId` on activate → 1 violation (id required)

## Step 4 — Self-check

Confirmed: Spring `@Valid` is not used in this pre-lab. Validation runs through a plain
`ValidatorFactory` / `Validator` from Jakarta Bean Validation.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.

## Fixtures (self-check)

| ID | Name | Status |
| -- | ---- | ------ |
| CUS-1001 | Amina Khan | ACTIVE |
| CUS-1002 | Ravi Singh | PROSPECT |

Correlation ID: `lab-request-001`