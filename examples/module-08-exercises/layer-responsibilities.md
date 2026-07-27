# Layer Responsibilities - Module 8 Exercise 4


## Step 1 / 2 - Task assignments

| Task | Layer |
| ---- | ----- |
| Accept future create-customer input | controller |
| Reject blank customer name | service |
| Find customer by ID | repository |
| Represent customer ID/name/status | entity |
| Represent create request fields | dto |
| Define customer-not-found failure | exception |
| Wire application objects later | config |

## Step 3 - Repairing the god controller

Bad: the controller validates every rule, edits the list directly, builds queries, and formats errors, so it does everyone's job.

Fixed flow:

Controller maps the request -> Service validates and orchestrates -> Repository saves or finds -> Service returns the result -> Controller maps the response.

Now each layer does one thing and hands off to the next.

## Step 4 - Why boundaries help

Because each layer has one job, you can test the service on its own with a fake repository instead of spinning up a database. You can swap the storage layer later (say a real database) without touching the controller, since the controller only talks to the service. Keeping HTTP details out of the business logic means the rules don't break when the transport changes. And when every responsibility has one clear home, new developers can find where things live fast.
