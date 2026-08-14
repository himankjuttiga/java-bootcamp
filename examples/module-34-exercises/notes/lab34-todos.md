# Lab 34 — Fill useState TODOs

## Step 1 — Paste

```tsx
const [name, setName] = useState("");
const [status, setStatus] = useState<'ACTIVE' | 'SUSPENDED'>("ACTIVE");
const [error, setError] = useState<string | null>(null);

function onSubmit(e: FormEvent) {
  e.preventDefault();
  if (!name.trim()) { setError("Name is required"); return; }
  // TODO Lab 35: POST to API
  console.log({ name, status, correlation: "lab-request-001" });
}

<input value={name} onChange={(e) => setName(e.target.value)} />
```

## Step 2 — Fill

Blanks filled: initial name `""`; status union `'ACTIVE' | 'SUSPENDED'`; initial status `"ACTIVE"`; error message `"Name is required"`; onChange setter `setName`.

## Step 3 — Amina seed

Alternate edit-form TODO: `const [name, setName] = useState("Amina Khan"); // TODO: seed from selected customer`

## Step 4 — Lift state note

TODO: `selectedCustomerId` lives in the parent list container (e.g. `App`), not in `CustomerCard` — the card only reports `onSelect(customerId)` upward.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
