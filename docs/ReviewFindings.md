### 1. Add auditLog annotation

AuditLog annotation is a method-only annotation. 
- Uses Around annotation,
- logs only on successful call
- either uses method name for logging, or uses name from parameter
- logs one of the parameters
- could it use springel to get item.id for logging. Perhaps relies on item.toString.

### 2. Reduce dependencies

May have as many test dependencies as necessary, but production dependencies could be minimized. Is aspect-maven plugin needed?

