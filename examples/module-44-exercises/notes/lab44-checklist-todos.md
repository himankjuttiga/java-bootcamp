# Release checklist

- [x] jarSha256 matches the Lab 43 `SHA256SUMS` (from the `crm-jar` artifact, not a local rebuild)
- [x] Staging smoke GET /api/customers?status=ACTIVE returns 200 (readiness UP first)
- [ ] Approver: instructor sign-off before the prod promote
- [ ] Watch window owner: me, first 30 min after promote
- [ ] Rollback owner (prior SHA recorded): me, prior jarSha256 noted before promote
- GO / NO-GO: GO once both smoke boxes are checked and the approver signs

**Self-mark:** Pass
