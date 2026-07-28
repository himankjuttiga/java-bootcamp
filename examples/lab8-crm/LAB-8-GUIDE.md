# Lab 8 Guide - Northstar CRM Skeleton

## Overview
Builds the Maven skeleton for the CRM: standard layout, seven layer packages, compile-ready stub classes, and standards docs. No runtime behavior yet.

## Compile and run
```
mvn clean compile
java -cp target/classes com.northstar.crm.Main
```

## Design decisions
- Layered packages so responsibilities are separated from day one.
- Stubs throw UnsupportedOperationException to prove the structure compiles before any behavior exists.
- DTOs kept separate from the entity so API shapes never leak into the storage model.
- Dependencies point inward only: controller -> service -> repository -> entity.