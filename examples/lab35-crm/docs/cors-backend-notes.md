# Backend CORS checklist (sibling Spring app)

The Spring CRM API is a separate project in this workspace (`examples/lab29-crm`, or your
latest Spring CRM such as `lab32-crm`). This lab only adds CORS to it; the React work lives
in `../crm-ui`.

## 1. Allowlist the Vite origin, never `*`

Installed in `examples/lab29-crm/src/main/java/com/northstar/crm/config/WebConfig.java` as a
`CorsConfigurationSource` bean. Spring Security consumes it through `http.cors(...)`, which
matters here: MVC-level `addCorsMappings` alone would not cover the security filter chain.
The reference copy in `backend/WebConfig.java` shows the plain MVC form for a CRM without
Spring Security.

Start the API with the Lab 35 profile so customer endpoints are reachable without a token:

```bash
cd ~/java-bootcamp/examples/lab29-crm
mvn spring-boot:run -Dspring-boot.run.profiles=lab35
```

```java
registry.addMapping("/api/**")
    .allowedOrigins("http://localhost:5173")
    .allowedMethods("GET", "POST", "PUT", "DELETE")
    .allowedHeaders("Content-Type", "Authorization", "X-Correlation-Id");
```

Why not `*`:

* `*` cannot be combined with credentials at all, so it breaks the moment Lab 36 adds tokens.
* `*` lets any page in the user's browser, including a hostile one, read authenticated CRM
  responses. The allowlist is the only thing standing between a logged-in session and
  `https://evil.example`.
* Preflight needs the methods and headers listed too. Omitting `X-Correlation-Id` from
  `allowedHeaders` fails the `OPTIONS` before the real request is ever sent.

## 2. Contract evidence (run with Spring up)

```bash
# list
curl -i http://localhost:8080/api/customers

# create with correlation id
curl -i -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -H "X-Correlation-Id: lab-request-001" \
  -d '{"id":"CUS-1003","name":"Nina Torres","email":"nina.torres@example.com","status":"PROSPECT"}'

# forced 400: invalid email
curl -i -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -H "X-Correlation-Id: lab-request-001" \
  -d '{"id":"CUS-1004","name":"Bad Email","email":"not-an-email","status":"PROSPECT"}'

# forced 404
curl -i http://localhost:8080/api/customers/CUS-9999
```

Expected: 200 list containing `CUS-1001` and `CUS-1002`; 201 with the created record; 400 with
a `violations` array naming `email`; 404 with the safe envelope. Paste the real headers and
bodies under "Recorded output" below.

## 3. Allowed origin preflight (should succeed)

```bash
curl -i -X OPTIONS http://localhost:8080/api/customers \
  -H "Origin: http://localhost:5173" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: content-type,x-correlation-id"
```

Expect `Access-Control-Allow-Origin: http://localhost:5173` and the requested method and
headers echoed back.

## 4. Hostile origin probe (must be denied)

```bash
curl -i -H "Origin: https://evil.example" http://localhost:8080/api/customers
```

Expect **no** `Access-Control-Allow-Origin: https://evil.example` in the response headers.
Note that the body may still come back on a plain `curl` request, because `curl` is not a
browser and enforces nothing. The proof is the absent header: without it, a real browser
refuses to hand the response to `https://evil.example`. A reflected evil `Origin` means the
allowlist is wrong or a permissive filter is overriding it.

## Recorded output

Paste real terminal output here when the API is running, and save the matching screenshots
under `notes/screenshots/lab-35/`.

```text
# TODO(evidence): paste GET /api/customers output
# TODO(evidence): paste 400 violations output
# TODO(evidence): paste allowed-origin preflight headers
# TODO(evidence): paste evil-origin probe headers (no ACAO for evil.example)
```
