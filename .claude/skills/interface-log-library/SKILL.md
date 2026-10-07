---
name: interface-log-library
description: Use when working on the interface-log library — the @InterfaceLog annotation, InterfaceLogAspect, InterfaceLogMapper, the log line format, parameter exclusion, stack trace and log level rules, or when a consumer project's interface logging behaves unexpectedly.
---

# interface-log library

## Overview

Spring AOP library. Annotate a Spring bean method with `@InterfaceLog`; `InterfaceLogAspect` logs one line per call through the target class's SLF4J logger.

**Core principle:** the annotation attributes and the log line are the public API. Consumers (dynamic-form, spring-boot-react-template) and their log parsing depend on them — keep them backward compatible.

## Technology Stack

- Spring Boot 3.1 parent (dependency management), Java 17
- `spring-boot-starter-aop` (proxy-based Spring AOP; optional `aspectj` Maven profile for compile-time weaving)
- Log4j2 via SLF4J (`spring-boot-starter-logging` excluded), Lombok, Commons Lang 3 / Collections 4
- Tests: JUnit 5, `SpringExtension`, `OutputCaptureExtension`, AssertJ

## Project Structure

```
src/main/java/org/example/log/
  InterfaceLog.java          # annotation: exclude, stackTrace, prefix
  InterfaceLogAspect.java    # @Around advice, formatting, level/stack trace rules
  InterfaceLogMapper.java    # merges class + method annotation
src/test/java/org/example/log/
  InterfaceLogAspectTest.java
  MethodAnnotationService.java, ClassAnnotationService.java   # annotated test beans
  TestConfig.java, User.java, Role.java
src/test/resources/log4j2-test.xml
```

## Behaviour Reference

Log line: `{prefix}{method} | OK|FAIL | {ms}ms | [name: value, ...] | {Exception(message)}`

| Rule | Behaviour |
|---|---|
| Pointcut | `@annotation(interfaceLog)` — only **methods** annotated with `@InterfaceLog` are intercepted. A class-level annotation alone intercepts nothing; it only supplies defaults |
| Class + method annotation | Merged by `InterfaceLogMapper`: non-blank method `prefix` / non-empty method `exclude` win, else class value |
| Parameter names | Read from `Method.getParameters()` (works on proxies); needs `-parameters`, set by the Spring Boot parent |
| `exclude` | Parameter names to omit |
| Level | `INFO` on success or on an exception declared in the method's `throws`; `WARN` on an undeclared exception |
| Stack trace | Attached only if `stackTrace = true` and the exception is not declared |
| Logger | Target class canonical name |

## Development Workflow

```bash
mvn test                 # unit tests
mvn -Paspectj test       # with compile-time AspectJ weaving
mvn install              # install locally for consumer projects
```

## Coding Conventions

- Google Java Style, two-space indentation
- Keep the aspect free of consumer-specific logic; no new runtime dependencies without need
- Prefer standard library and Commons helpers over hand-rolled loops
- Comments explain *why* (proxy limitations, AOP quirks)
- Clean Code: small methods, intention-revealing names, no duplication
- Effective Java: favor immutability, standard exceptions, never swallow exceptions — the advice must rethrow what `proceed()` throws

## Testing

- Every behaviour change gets a test in `InterfaceLogAspectTest` using `CapturedOutput` and `assertThat(output).contains(...)`
- Add annotated methods to `MethodAnnotationService` (method-level) or `ClassAnnotationService` (class + method)
- Test names describe the behaviour (`logExpectedException`, `logNullParameter`)

## Common Mistakes

- Expecting a class-level `@InterfaceLog` to log un-annotated methods
- Self-invocation inside a bean — Spring AOP proxies are bypassed, nothing is logged
- Using `MethodSignature.getParameterNames()` — returns null on proxies
- Changing the log line format without checking consumers
