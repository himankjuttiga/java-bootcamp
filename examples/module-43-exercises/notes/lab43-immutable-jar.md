# Package-once

- Build the JAR once, only on main/tags, in the `package` job (`needs: verify`).
- `mvn -B -ntp -DskipTests package`. Tests already ran in verify, do not repeat them here.
- Write a checksum next to the JAR: `sha256sum target/*.jar > target/SHA256SUMS`.
- Record the commit: `echo "commit=${GITHUB_SHA}" >> target/SHA256SUMS`.
- Upload `target/*.jar` and `target/SHA256SUMS` as artifact `crm-jar`.
- Deploy (Lab 44) consumes this artifact and must NOT `mvn package` again, so the deployed bytes are the same bytes that passed verify.
