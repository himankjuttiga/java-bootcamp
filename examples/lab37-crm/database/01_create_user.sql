-- Lab 37 — least-privileged application role
-- Run as the database owner / superuser (compose creates POSTGRES_USER for that job).
--   psql "host=localhost port=5432 dbname=crm user=crm" -f database/01_create_user.sql
--
-- Password is passed in, never hardcoded:
--   psql ... -v crm_app_password="$CRM_APP_PASSWORD" -f database/01_create_user.sql
-- CRM_APP_PASSWORD lives in .env, which is gitignored.

\set ON_ERROR_STOP on

-- Idempotent: re-running the script must not fail on a second pass.
-- psql substitutes :variables in plain SQL but NOT inside dollar-quoted blocks, so the role is
-- created with a psql conditional rather than a DO block. That keeps the password out of the
-- script and out of Git.
SELECT NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'crm_app') AS crm_app_missing \gset

\if :crm_app_missing
CREATE ROLE crm_app LOGIN PASSWORD :'crm_app_password';
\endif

-- The app role owns its own schema and nothing else. No SUPERUSER, no CREATEDB, no CREATEROLE:
-- an application credential with DBA rights turns one SQL injection into total loss.
CREATE SCHEMA IF NOT EXISTS crm_app AUTHORIZATION crm_app;

GRANT CONNECT ON DATABASE crm TO crm_app;
GRANT USAGE, CREATE ON SCHEMA crm_app TO crm_app;

-- Keep the app off the public schema entirely (PostgreSQL 15+ already restricts it).
REVOKE ALL ON SCHEMA public FROM PUBLIC;

-- Resolve unqualified names to crm_app for every future session of this role.
ALTER ROLE crm_app SET search_path = crm_app;

SELECT rolname, rolsuper, rolcreatedb, rolcreaterole, rolcanlogin
FROM pg_roles
WHERE rolname = 'crm_app';
