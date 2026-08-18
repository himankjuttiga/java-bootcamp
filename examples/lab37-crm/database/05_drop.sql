-- Lab 37 cleanup — run as crm_app. Children before parents.
--   psql "host=localhost port=5432 dbname=crm user=crm_app" -f database/05_drop.sql
--
-- Dropping customer first fails while children reference it. That dependency error is the
-- foreign key doing its job, not a defect in the script.
--
-- PostgreSQL syntax only: Oracle's CASCADE CONSTRAINTS PURGE does not exist here.

\set ON_ERROR_STOP on

DROP TABLE IF EXISTS customer_status_history;
DROP TABLE IF EXISTS address;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS customer;
