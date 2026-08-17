# Lab 35 — Fill Fetch TODOs

## Step 1 — Paste

```ts
export type Customer = { customerId: string; name: string; status: string };

const BASE_URL = import.meta.env.VITE_CRM_API_URL ?? "http://localhost:8080";
const CUSTOMERS_URL = `${BASE_URL}/api/customers`;

export async function listCustomers(signal?: AbortSignal): Promise<Customer[]> {
  const res = await fetch(CUSTOMERS_URL, {
    headers: { "X-Correlation-Id": "lab-request-001" },
    signal,
  });
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return (await res.json()) as Customer[];
}

export async function getCustomer(id: string): Promise<Customer> {
  const res = await fetch(`${CUSTOMERS_URL}/${id}`, {
    headers: { "X-Correlation-Id": "lab-request-001" },
  });
  // TODO: handle 404 for unknown id -> throw ApiError(404, "Customer not found")
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return (await res.json()) as Customer;
}
```

## Step 2 — Fill

Blanks filled: URL `${BASE_URL}/api/customers` (base `http://localhost:8080`, path `/api/customers`); correlation header value `lab-request-001`; return type `Customer[]`; `getCustomer` reuses the same `CUSTOMERS_URL` base so the two calls cannot drift apart.

TODO list for the single HTTP boundary:

| TODO | Why it exists |
| --- | --- |
| `ApiError` class carrying `status`, `detail`, `correlationId` | one error type the UI can switch on |
| `http()` helper wrapping `fetch` | one place to add `Authorization` in Lab 36 |
| `customersApi` module (`listCustomers`, `getCustomer`, `createCustomer`) | components never call `fetch` themselves |
| `BASE_URL` read from `import.meta.env.VITE_CRM_API_URL` | no hard-coded host in components |
| 204 guard in `http()` | empty body means skip `res.json()` |

`VITE_CRM_API_URL` is read once at module load through `import.meta.env`. Vite inlines env values at dev-server start, so editing `.env` without restarting Vite leaves the old value baked in and the change appears to do nothing.

## Step 3 — UI TODO

```ts
// TODO: on success setCustomers including Amina + Ravi fixtures from API
```

## Step 4 — Error TODO

```ts
// TODO: map 400 body.detail to form error string
```

Note: the lab uses the `fetch` helper rather than Axios. The concepts carry over one to one (interceptor becomes the `http()` wrapper, `axios.isCancel` becomes the `AbortError` check), and `res.ok` must be checked before `res.json()` because `fetch` does not reject on 4xx or 5xx.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
