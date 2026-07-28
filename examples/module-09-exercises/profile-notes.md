# Profiles Notes - Module 9 Exercise 2

How dev and prod profiles keep laptop settings from silently becoming production settings.

## Step 1 / 2 - Reading the profiles

| Question | Answer |
| -------- | ------ |
| Active profile on plain mvn package | dev (activeByDefault) |
| Activate prod on the command line | mvn -Pprod package |
| dev app.env value | dev |
| prod app.env value | prod |

## Step 3 - Spot the mistakes

- **Production passwords inside the dev profile:** secrets end up in source and get used on laptops, which is a leak and a security risk.
- **Making prod activeByDefault on every laptop:** engineers would accidentally run production settings locally, which is dangerous.
- **Assuming profiles change Java package names:** they don't. Profiles only swap build and config properties, not code or package structure.
- **Documenting secrets in screenshots of profile properties:** the secret is now visible in an image that gets shared or committed.

## Step 4 - Activation rule

Keep dev as the laptop default. Activate prod intentionally with -Pprod. Never store real production secrets in pom.xml profiles.
