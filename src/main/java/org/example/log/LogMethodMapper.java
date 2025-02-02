package org.example.log;

import java.lang.annotation.Annotation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class LogMethodMapper implements LogMethod {
  private LogMethod classLogMethod;
  private LogMethod logMethod;

  public LogMethodMapper(LogMethod classLogMethod, LogMethod logMethod) {
    this.classLogMethod = classLogMethod;
    this.logMethod = logMethod;
  }

  @Override
  public Class<? extends Annotation> annotationType() {
    return LogMethod.class;
  }

  @Override
  public String printer() {
    return StringUtils.isNotEmpty(logMethod.printer())
        ? logMethod.printer()
        : classLogMethod.printer();
  }

  @Override
  public String prefix() {
    return StringUtils.isNotEmpty(logMethod.prefix())
        ? logMethod.prefix()
        : classLogMethod.prefix();
  }

  @Override
  public boolean logStackTrace() {
    return !logMethod.logStackTrace() ? logMethod.logStackTrace() : classLogMethod.logStackTrace();
  }

  @Override
  public String logName() {
    return StringUtils.isNotEmpty(logMethod.logName())
        ? logMethod.logName()
        : classLogMethod.logName();
  }

  @Override
  public String[] excludeExceptions() {
    return ArrayUtils.isNotEmpty(logMethod.excludeExceptions())
        ? logMethod.excludeExceptions()
        : classLogMethod.excludeExceptions();
  }

  @Override
  public String[] exclude() {
    return ArrayUtils.isNotEmpty(logMethod.exclude())
        ? logMethod.exclude()
        : classLogMethod.exclude();
  }
}
