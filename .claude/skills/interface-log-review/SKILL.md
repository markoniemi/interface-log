---
name: interface-log-review
description: "Use when reviewing Java code or a diff in interface-log against the project's coding standards (Google Java Style, Spring AOP patterns, backward-compatible library API). Invoke with a file path or paste code to review."
---

# interface-log Code Review

Review the specified file against this project's standards. If no file specified, ask user which file to review.

## How to review

1. Read target file(s).
2. Check the checklist below.
3. Report findings grouped by severity:
   - **Must fix** — correctness, broken API compatibility, or hard project rules
   - **Should fix** — style or maintainability issues
   - **Consider** — optional improvements
4. For each finding: file path + line number, rule violated, one-line fix.
5. Brief summary paragraph.

Do NOT rewrite whole file. Propose targeted edits only.

---

## Checklist

### Style & Formatting
- [ ] Two-space indentation; clear names; no commented-out code; comments explain why

### Library API
- [ ] Annotation attributes and defaults unchanged, or change is backward compatible
- [ ] Log line format and levels unchanged, or consumers (dynamic-form, spring-boot-react-template) checked
- [ ] No new runtime dependencies without need; unused dependencies removed

### Aspect Correctness
- [ ] Advice rethrows exactly what `proceed()` throws; return value passed through
- [ ] Works on Spring proxies (no `MethodSignature.getParameterNames()`)
- [ ] Null parameters and empty parameter lists handled
- [ ] No sensitive data logged by default; `exclude` honoured per parameter

### Exceptions
- [ ] Standard Java exceptions; none swallowed silently

### Testing
- [ ] Behaviour change covered in `InterfaceLogAspectTest` with `CapturedOutput`
- [ ] Both method-level and class+method annotation cases covered where relevant
- [ ] Test names describe behaviour

### Single Responsibility
- [ ] One reason to change per class/method
- [ ] Methods ≤ ~20 lines; no DRY violations

### Utility Libraries
- [ ] Apache Commons for null/empty checks instead of manual conditionals

### Dependencies
- [ ] Check if dependencies have newer versions or are unmaintained (`mvn versions:display-dependency-updates`, Maven Central, GitHub activity)
