# Lab 22 readiness checklist

| File | Present? (yes/no) |
| ---- | ----------------- |
| notes/ioc-vs-new.md | yes |
| notes/constructor-di.md | yes |
| notes/lab22-lifecycle-notes.md | yes |
| notes/stereotype-map.md | yes |
| notes/bean-graph-sketch.md | yes |

## Scope

Pre-lab only. Primary DI style for lab? Constructor injection with `final` fields.

## Self mark

Overall prep: Pass
If Fail, revisit: the exercise for whichever note is missing or still has a blank

## Debug / design challenge

If the constructor-di notes still prefer field `@Autowired`, reopen Exercise 2 (Constructor Injection Preference).

## Predict the output / behavior

`CrmApplication` should live in the root package `com.northstar.crm`, so its `@SpringBootApplication` component scan covers all sub-packages (`api`, `service`, `repository`, etc.) and finds every CRM bean.
