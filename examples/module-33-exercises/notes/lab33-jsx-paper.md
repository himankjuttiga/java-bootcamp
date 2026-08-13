# Lab 33 — JSX on Paper

## Step 1 — Tree

```
<CustomerList>
  <CustomerCard customer={amina} onEdit={onEdit} />
  <CustomerCard customer={ravi} onEdit={onEdit} />
</CustomerList>
```

## Step 2 — Keys

`key={customerId}` should be `CUS-1001`, not the array index, because the key is React's identity for that list item. An index key ties identity to position — if the list is later sorted or filtered, React reuses the wrong DOM node (and any attached focus/state) for the wrong customer. `customerId` stays attached to the same person regardless of order.

## Step 3 — Badge

```
<CustomerCard customer={amina}>
  ...
  <StatusBadge status="ACTIVE" />
</CustomerCard>
```

## Step 4 — No runtime

Do not create a Vite app in this exercise.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
