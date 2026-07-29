# Lab 10 Pre-lab - Weak vs Strong Copilot Prompts

## Step 1 - Weak prompt

> Write a customer class.

Why it is weak: Copilot has to guess everything. It might put the class in the wrong package, add Spring or JPA annotations we do not use yet, pick fields we never asked for, or assume a different JDK. There is nothing telling it what "customer" means for our project.

## Step 2 - Strong prompt

> Write a plain Java 21 class for a Northstar CRM customer. Example: CUS-1001, Amina Khan, status ACTIVE. Fields: id (String), fullName (String), status (String). No Spring, no JPA, no annotations. Add the correlation note lab-request-001 in a comment only.

## Step 3 - Three constraints the strong prompt adds

1. JDK / language: plain Java 21, no frameworks.
2. Domain fixtures: real example data (CUS-1001, Amina Khan, ACTIVE) and the exact fields.
3. No phantom dependencies: explicitly no Spring, no JPA, no invented annotations.

## Notes

- Only fake CRM ids are used (CUS-1001 / CUS-1002), never real data or secrets.
- Never accept the first suggestion blindly; the constraints keep Copilot on track.

