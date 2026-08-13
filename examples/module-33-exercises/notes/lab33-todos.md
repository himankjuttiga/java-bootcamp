# Lab 33 — Fill Component TODOs

## Step 1 — Paste

```tsx
type CustomerCardProps = {
  customerId: string;
  name: string;
  status: 'ACTIVE' | 'SUSPENDED';
  onSelect: (id: string) => void;
};

export function CustomerCard({ customerId, name, status, onSelect }: CustomerCardProps) {
  return (
    <article aria-label={`${name} (${customerId})`}>
      <h3>{name}</h3>
      <StatusBadge status={status} />
      <button type="button" onClick={() => onSelect(customerId)}>View</button>
    </article>
  );
}
```

## Step 2 — Fill

Blanks filled: status union `'ACTIVE' | 'SUSPENDED'`; aria-label `` `${name} (${customerId})` ``; heading `name`; `onSelect` argument `customerId`.

## Step 3 — Sample usage

`<CustomerCard customerId="CUS-1001" name="Amina Khan" status="ACTIVE" onSelect={(id) => console.log(id, "lab-request-001")} />`

## Step 4 — A11y note

The button has visible text ("View"), not an icon alone — avoid icon-only controls without an `aria-label`.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
