# Claude Development Guidelines

This document provides development guidelines for interface-log, a small Spring AOP library that logs method calls annotated with `@InterfaceLog`.

## Quick Reference

- **Skills**: `.claude/skills/` — `interface-log-library` (annotation, aspect, log format), `interface-log-review` (coding standards checklist), `test`

## Consumers

The library (`org.example:interface-log`, published to repsy) is used by the backends of [dynamic-form](../../dynamic-form) and [spring-boot-react-template](../../spring-boot-react-template) on controllers and services. When changing anything consumers depend on, check them, especially:
- Annotation attributes (`exclude`, `stackTrace`, `prefix`) and their defaults
- Log line format and log levels
- Spring Boot / AOP compatibility with the consumers' Spring Boot versions

## IntelliJ MCP Tool Preferences

When working with this project, prefer IntelliJ IDE MCP tools (`mcp__idea__*`) over generic alternatives:

- **File operations**: Use `mcp__idea__read_file`, `mcp__idea__create_new_file` instead of Bash/generic Read/Write
- **Project navigation**: Use `mcp__idea__search_symbol`, `mcp__idea__search_text`, `mcp__idea__list_directory_tree` for code exploration
- **Building/testing**: Use `mcp__idea__build_project`, `mcp__idea__execute_run_configuration` instead of manual Maven commands
- **Refactoring**: Use `mcp__idea__rename_refactoring` for safe renames across the project
- **Git operations**: Use `mcp__idea__git_status` to check repo state
- **Linting/diagnostics**: Use `mcp__idea__get_file_problems`, `mcp__idea__lint_files` for code quality checks
- **Debugging**: Use `mcp__idea__xdebug_*` tools for stepping through code when needed

IntelliJ MCP tools are aware of the project structure, dependencies, and IDE state, making them more reliable than shell-based alternatives.

## Architecture Overview

Single Maven module, Spring Boot 3.1 parent, Java 17, Log4j2 via SLF4J.

- `InterfaceLog` — annotation for methods and classes
- `InterfaceLogAspect` — `@Around` advice that logs method name, result, duration, parameters and exception
- `InterfaceLogMapper` — merges a class-level annotation with the method-level one

CI: GitHub Actions (`.github/workflows/maven-build.yaml`, deploys to repsy) and Azure Pipelines (`azure-pipelines.yml`).

## Git Commit Messages

Commit messages use a **one-line format with semicolons** to separate concerns:

```
Brief action; additional change; optional note
```

**Examples:**
- `Fix implementation when annotation is used on proxy class; test with proxy`
- `Simplify InterfaceLog annotation and implementation`

**Guidelines:**
- One line only — concise and scannable in git log
- Use semicolons to separate multiple logical changes
- Use imperative mood: "add", "fix", "refactor" (not "added", "fixed")
- Focus on **what changed and why**, not implementation details
- Capitalize first word
- No period at end

## Implementation Tasks

During implementation tasks (planning, coding, testing):
- **Do not commit** unless explicitly asked
- Work iteratively and validate completeness before committing
- Use feature branches for significant work
- Plan all changes upfront before execution
