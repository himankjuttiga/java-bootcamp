# dockerignore + env

Ignore: `target/`, `.git/`, `.env`, `notes/`, `.idea/`, `*.iml` (do not ignore the Dockerfile)
.env.example keys: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` (placeholder), `JAVA_OPTS`
Never: password in Dockerfile — inject at run time via `--env-file .env`
