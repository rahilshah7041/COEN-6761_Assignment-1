Failure Semantics Report - Rahil Shah(Student Id - 40329714)

1. Fail-Fast (Atomic Policy)
Behavior: If one service fails, everything fails.
Pros: You don't get incomplete data.
Cons: The whole system stops for one small error.

2. Fail-Partial (Best-Effort Policy)
Behavior: Only returns the successes and skips the failures.
Pros: The system keeps working even if a service is down.
Cons: The list of results might be shorter than expected.

3. Fail-Soft (Fallback Policy)
Behavior: Replaces errors with a fallback value.
Pros: You always get back the same amount of data.
Cons: It can hide the fact that a service is actually broken.