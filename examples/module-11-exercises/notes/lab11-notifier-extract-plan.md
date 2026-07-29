# Notifier extract plan -- CustomerService.activate

## Step 1 -- Smell: I/O buried inside the service method
System.out.println("Activated " + id);        // testability smell
emailClient.send(customer.getEmail(), ...);    // hidden side effect

The service method welds business logic to side effects. A test cannot
observe or intercept the print/email without actually printing and sending,
so the unit resists isolation. That is the testability smell.

## Step 2 -- Extract sketch (paper only, do not implement yet)
interface CustomerNotifier { void notifyActivated(String customerId); }

CustomerService depends on the CustomerNotifier interface instead of calling
System.out / emailClient directly. In a test, a mock CustomerNotifier can be
injected and verified without any real I/O.

## Step 3 -- Why this matters for Copilot
Naming the collaborator (CustomerNotifier) in the prompt stops the AI from
burying I/O back inside the service and produces a mockable seam instead.

## Step 4 -- Out of scope for now
Do not implement Spring events or Kafka yet. This is a prep sketch only --
the interface seam is the deliverable, not a messaging system.