# Review Findings

Findings from reviewing interface-log while setting up the Claude configuration. Nothing here has been fixed yet.

## Must fix

### 1. `exclude` drops all parameters instead of the named ones

`InterfaceLogAspect.java:90` and `:108-111`

`isLogged(parameterNames, interfaceLog.exclude())` is called inside the loop but checks the *whole* parameter name array against `exclude`, not the current parameter. Excluding a single parameter therefore hides every parameter of the method.

The `skipParameters` test does not catch this because it excludes all parameters of the method (`user`, `anotherParameter`).

**Fix:** check per parameter, e.g. `!ArrayUtils.contains(interfaceLog.exclude(), parameterNames[i])`, and add a test that excludes only one of two parameters (e.g. a password) and asserts the other is still logged.

### 2. Duplicate names in `exclude` throw at log time

`InterfaceLogAspect.java:111`

`Set.of(skipParameters)` throws `IllegalArgumentException` on duplicate elements, so `@InterfaceLog(exclude = {"a", "a"})` makes every call of the annotated method fail after it has already run. Fixed as a side effect of finding 1.

### 3. Class-level `stackTrace` overrides method-level `stackTrace = true`

`InterfaceLogMapper.java:29-33`

```java
return !methodAnnotation.stackTrace()
    ? methodAnnotation.stackTrace()   // method false -> false
    : classAnnotation.stackTrace();   // method true  -> class value
```

With the class at `stackTrace = false` and the method at `stackTrace = true`, the result is `false`. Because annotation attributes cannot tell "not set" from "set to the default", the method value cannot reliably override the class value for booleans either way.

**Fix:** decide the intended rule (e.g. `method || class`, or make the attribute a tri-state enum `DEFAULT / TRUE / FALSE`) and add tests for both combinations.

## Should fix

### 4. Expected-exception check misses subclasses and overloads

`InterfaceLogAspect.java:67-71`

`isExpectedException` compares canonical class names for equality, so a subclass of a declared exception (e.g. `FileNotFoundException` for `throws IOException`) is logged as unexpected (`WARN`, with stack trace). It also scans every declared method with the same name, so an overload's `throws` clause can make an exception count as expected.

**Fix:** use `((MethodSignature) joinPoint.getSignature()).getMethod().getExceptionTypes()` and `declared.isAssignableFrom(e.getClass())`.

### 5. Unused dependencies and build configuration

`pom.xml`
- MapStruct annotation processors (`mapstruct-processor`, `mapstruct-spring-extensions`) and `org.mapstruct.version` — no mappers in the code
- `spring-cloud-dependencies` BOM and `spring-cloud.version` — no Spring Cloud dependencies
- `spring-security-config` / `spring-security-test` — no Spring Security usage in `src`
- `spring.boot.version` imported as a BOM again although the parent already manages it
- Lombok processor version hardcoded (`1.18.34`) instead of the parent-managed version
- `aspectj` profile uses `complianceLevel` / `source` / `target` 16 while the project is Java 17

### 6. Unused pointcut

`InterfaceLogAspect.java:24`

`interfaceLog()` pointcut is declared but the advice uses its own `@annotation(interfaceLog)` expression.

## Consider

### 7. Outdated versions

Spring Boot 3.1.0 and Java 17. Consumers (dynamic-form, spring-boot-react-template) run newer Spring Boot versions; upgrading keeps AOP and SLF4J versions aligned.

### 8. Class-level annotation alone logs nothing

The pointcut `@annotation(interfaceLog)` only matches annotated methods; a class-level `@InterfaceLog` only provides defaults. Either document this in a README or extend the pointcut with `@within(InterfaceLog)` so annotating a class logs all its public methods.
