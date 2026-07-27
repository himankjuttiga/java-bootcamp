# Maven Layout Notes - Module 8 Exercise 1

Where each type of file belongs in a standard Maven project.

## Step 1 / 2 - File classification

| File | Destination |
| ---- | ----------- |
| Customer.java | src/main/java/com/northstar/crm/ |
| CustomerServiceTest.java | src/test/java/com/northstar/crm/ |
| application.properties | src/main/resources/ |
| sample-customers.json (used only by tests) | src/test/resources/ |
| CODING-STANDARDS.md | docs/ |
| Customer.class | generated under target/classes/ (not written by hand) |

## Step 3 - Why target/ is ignored

target/ is generated from source by Maven every time you build. It can be deleted and rebuilt anytime, so there is no reason to commit it. It goes in .gitignore.

## Step 4 - Spot the mistakes

- **Production Java in src/test/java:** it won't ship in the real build and other main code can't use it, since test code isn't on the main classpath.
- **Passwords committed in application.properties:** anyone with repo access sees the secret, and it stays in git history forever. Use environment variables or a secrets manager instead.
- **Hand-editing target/classes:** those are compiled files that get wiped on the next build, so any edit is lost. Change the source instead.
- **Test fixtures in production resources:** they get packaged and shipped with the app for no reason, which bloats it and can leak test data. Keep them in src/test/resources.
