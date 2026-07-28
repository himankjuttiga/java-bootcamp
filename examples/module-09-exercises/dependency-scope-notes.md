# Dependency Scopes Notes - Module 9 Exercise 4

Assigning the correct Maven scope so libraries land on the right classpath.

## Step 1 / 2 - Scope assignments

| Dependency need | Scope |
| --------------- | ----- |
| JUnit Jupiter used only in src/test/java | test |
| Spring Context API called from production sources | compile (default) |
| JDBC driver not imported in source but needed at runtime | runtime |
| API the app server will provide in production | provided |

## Scope map

| Scope | Compile classpath | Runtime classpath | Use |
| ----- | ----------------- | ----------------- | --- |
| compile (default) | Yes | Yes | Libraries called from production code |
| test | Tests only | Tests only | JUnit, Mockito, test helpers |
| runtime | No | Yes | Drivers needed to run but not compile against |
| provided | Yes | No (container supplies) | Servlet API, server-provided APIs |

## Step 3 - Why JUnit without a scope is wrong

Leaving JUnit on the default compile scope makes it a production dependency. It gets packaged and resolved for the main app, pollutes the runtime classpath, and signals the wrong intent to teammates and CI.

## Step 4 - Team rule

Test libraries always use scope test. Do not leave JUnit on the default compile scope.

