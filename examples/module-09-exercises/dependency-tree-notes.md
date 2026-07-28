# Dependency Tree Notes - Module 9 Exercise 5

Direct vs transitive dependencies, and why CI prefers mvn -B verify.

## Step 1 / 2 - Classifying the rows

| Artifact | Direct or transitive? | Scope |
| -------- | --------------------- | ----- |
| junit-jupiter | Direct (you declared it) | test |
| junit-jupiter-params | Transitive (comes with Jupiter) | test |

## Terms

- Direct dependency: declared in your own pom.xml.
- Transitive dependency: pulled in because a direct dependency needs it.
- Scope column (:test): where that artifact is visible.

## Step 3 - Running the tree (after Exercise 6)

Once the mini POM exists, run from mini-maven/:

```
mvn -q dependency:tree
```

Optional, saves a file:

```
mvn -q dependency:tree -DoutputFile=dependency-tree.txt
```

JUnit should appear with the :test scope, confirming it stays off the production classpath.

## Step 4 - CI command habit

| Question | Answer |
| -------- | ------ |
| What does -B mean? | Batch mode, fewer interactive prompts, cleaner CI logs |
| Why verify instead of install on every push? | Proves package plus checks without writing into every agent's ~/.m2 unless the pipeline intentionally installs |
| Preferred CI-style command | mvn -B verify |

README sentence: Teammates and CI should reproduce the build with mvn -B verify.
