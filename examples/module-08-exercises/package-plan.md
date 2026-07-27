# Package Plan - Module 8 Exercise 2

Mapping the seven CRM types to packages and fully qualified names. Root package is com.northstar.crm.

## Step 1 / 2 - Type to package map

| Type | Package | Fully qualified name |
| ---- | ------- | -------------------- |
| CustomerController | com.northstar.crm.controller | com.northstar.crm.controller.CustomerController |
| CustomerService | com.northstar.crm.service | com.northstar.crm.service.CustomerService |
| CustomerRepository | com.northstar.crm.repository | com.northstar.crm.repository.CustomerRepository |
| Customer | com.northstar.crm.entity | com.northstar.crm.entity.Customer |
| CustomerRequest | com.northstar.crm.dto | com.northstar.crm.dto.CustomerRequest |
| AppConfig | com.northstar.crm.config | com.northstar.crm.config.AppConfig |
| CustomerNotFoundException | com.northstar.crm.exception | com.northstar.crm.exception.CustomerNotFoundException |

## Step 3 - Package to path

package com.northstar.crm.service; -> src/main/java/com/northstar/crm/service/

CustomerRequest path: src/main/java/com/northstar/crm/dto/CustomerRequest.java

## Step 4 - Fixing bad names

| Bad | Correct |
| --- | ------- |
| com.Northstar.CRM.Service | com.northstar.crm.service |
| package utils for customer business rules | service (or a focused domain package) |
| customer_service.java | CustomerService.java |
| package declaration doesn't match folders | make the declaration and folder path identical |

## Why

Packages are lowercase and reverse-domain, classes are PascalCase, and each package is named for its responsibility (controller, service, repository) instead of a vague dumping ground like util or misc. The folder path must always match the package declaration or it won't compile.
