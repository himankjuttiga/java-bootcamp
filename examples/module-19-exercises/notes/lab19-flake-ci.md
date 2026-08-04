# Lab 19 — Flake and CI Note

## Flake sources
1. Timing: acting before the element is ready (animations, async status updates), so a fixed Thread.sleep either races the UI or wastes time.
2. Shared mutable CRM data: tests reusing the same customer (e.g. CUS-1002) so one run's state leaks into the next, making order-dependent passes/failures.

## Mitigation
Isolated fixtures per test, stable data-testid locators, and explicit waits (wait for customer-status to be visible) instead of sleeps.

## CI constraint
Run headless, and align the browser and driver versions via WebDriverManager (or a matched Chrome/chromedriver pair) so CI agents match green laptops.

## Scope
Pre-lab only.