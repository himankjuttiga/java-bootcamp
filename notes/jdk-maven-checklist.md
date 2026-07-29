# JDK 21 / Maven Checklist - Module 10 Exercise 5

## Step 1 - Version checks

```
java -version   # must read 21.x, not 17 or 11
mvn -version    # Java home must point at a JDK 21
```

## Step 2 - PATH trap (Windows)

If an older JDK sits before JDK 21 on the Windows PATH, java -version reports the wrong version even though 21 is installed. Fix habit: reorder PATH so JDK 21 comes first, and set JAVA_HOME to the JDK 21 folder. Do not assume, always run the check.

(Note from Lab 9: my machine had JDK 26 as the default, which broke the JUnit test framework. Fixed by setting the IntelliJ project SDK and the Maven runner JRE to Temurin 21.)

## Step 3 - Workspace

Prep files for this module live in the bootcamp workspace under notes\ (examples/module-10-exercises/notes/).

## Step 4 - Out of scope

Do not run the full lab Maven goals until the timed Lab 10 session.
