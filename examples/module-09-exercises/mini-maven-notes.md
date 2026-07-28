# Mini Maven Notes - Module 9 Exercise 6

Evidence for the build-demo mini project.

## Test

Ran mvn test (via IntelliJ Maven). BUILD SUCCESS. Surefire ran one test, greetingMatchesBanner, which passed.

## Package

Ran mvn package on build-demo. BUILD SUCCESS. target/build-demo.jar was created with com.northstar.crm.BuildDemo set as the Main-Class in the manifest.

## Run

Output from running BuildDemo:

```
BuildDemo ready for Lab 9
```

Process finished with exit code 0.

## Confirms

- JUnit Jupiter is scope test, so it stays off the production classpath.
- Compiler release is 21.
- The JAR is runnable because the jar plugin set the Main-Class.
