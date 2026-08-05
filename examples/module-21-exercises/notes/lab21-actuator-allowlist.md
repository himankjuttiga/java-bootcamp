# Lab 21 — Actuator Allow-List

## Candidates

health, info, metrics, prometheus, env, beans, configprops, loggers, mappings, threaddump.

## Lab allow

Expose `health` (+ `info`) and `metrics` (plus `prometheus` for scrape demos). These are enough to show probes and Micrometer counters.

## Lock / deny

Never expose `env`, `beans`, `configprops`, `loggers`, `mappings`, or `threaddump` — they leak configuration, credentials, and internals.

## Prod auth note

`exposure.include` only controls *reachability*; it does not authenticate anyone. In production, put management endpoints behind authN/authZ, bind them to a separate management port or cluster-internal network, and apply least privilege with audited access. **Lab exposure is not production exposure.**

## Debug / design challenge

No — `/actuator/beans` should not be on the lab allow-list even for the graded demo. It dumps the full bean graph and configuration surface, adds nothing to a health/metrics demo, and builds the wrong habit. Keep the allow-list to health, info, metrics.

## Predict the output / behavior

`exposure.include=*` in production YAML exposes *every* Actuator endpoint over the web, including `env`, `configprops`, `threaddump`, and `heapdump` — a direct path to leaking secrets, environment variables, and internal state to anyone who can reach the port. It is a credential leak waiting to happen.

## Scope

Pre-lab only.
