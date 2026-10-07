---
name: test
description: "Use when asked to run the interface-log tests (JUnit) and report a summary."
---

Run the unit tests (surefire), then report a summary of results. The project has no integration tests.

Run from the repository root:

```bash
mvn test 2>&1 | grep -E "Tests run:|FAIL|ERROR|BUILD"
```

Before reporting, check that `target/surefire-reports/*.txt` were modified by this run. If they are stale, say so and do not report the tests as passing.

After running, summarize:
- How many tests passed / failed / skipped
- List any failing tests with their error messages
- If all tests pass, confirm with a single line
