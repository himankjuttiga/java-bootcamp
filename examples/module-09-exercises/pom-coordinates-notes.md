# POM Coordinates Notes - Module 9 Exercise 1

How Maven names a project so everyone resolves the same artifact.

## Step 1 / 2 - Reading the coordinates

| Question | Answer |
| -------- | ------ |
| groupId | com.northstar |
| artifactId | customer-service |
| version | 0.1.0-SNAPSHOT |
| packaging | jar |
| GAV | com.northstar:customer-service:0.1.0-SNAPSHOT |

## Step 3 - What SNAPSHOT means

A -SNAPSHOT version means the artifact is still under active development and may change without a new release number.

## Step 4 - Spot the mistakes

- **groupId com.example while packages are com.northstar.crm:** the coordinates should match the org, so com.example is misleading and inconsistent with the code.
- **artifactId CustomerService (PascalCase):** artifactIds are lowercase with hyphens, so it should be customer-service.
- **Omitting packaging and assuming WAR:** this is a plain app/library, so it should be jar, not war. Leaving it out and guessing wrong breaks the build output.
- **A different version on every laptop:** the whole team must agree on one version, or CI and teammates resolve different artifacts and things break.