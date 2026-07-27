# Dependency Direction - Module 8 Exercise 6

Checking which package dependencies are fine and which point the wrong way.

## Step 1 / 2 - Classifying the dependencies

| Dependency | Decision | Why |
| ---------- | -------- | --- |
| controller to service | Acceptable | normal inward flow |
| service to repository | Acceptable | normal inward flow |
| repository to entity | Acceptable | repository returns entities |
| entity to controller | Problematic | domain would depend on transport |
| repository to controller | Problematic | persistence would depend on presentation |
| service to DTO | Needs context | fine for this lab's simple mapping, but watch for transport leaking inward |
| DTO to repository | Problematic | a boundary model should not do storage |

## Step 3 - The cycle

Bad: controller -> service -> repository -> controller

That's a cycle. Changes ripple both ways, you can't test one package on its own, and it's unclear who owns what.

Fixed: controller -> service -> repository -> entity

Now everything points inward and stops.

## Step 4 - Architecture rule

Higher-level request handling may call inward services and repositories. Domain/entity and repository packages must not import controller classes.