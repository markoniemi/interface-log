package org.example.log;

import java.lang.annotation.Annotation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class InterfaceLogMapper implements InterfaceLog {
  private InterfaceLog classAnnotation;
  private InterfaceLog methodAnnotation;

  public InterfaceLogMapper(InterfaceLog classAnnotation, InterfaceLog methodAnnotation) {
    this.classAnnotation = classAnnotation;
    this.methodAnnotation = methodAnnotation;
  }

  @Override
  public Class<? extends Annotation> annotationType() {
    return InterfaceLog.class;
  }

  @Override
  public String printer() {
    return StringUtils.isNotEmpty(methodAnnotation.printer())
        ? methodAnnotation.printer()
        : classAnnotation.printer();
  }

  @Override
  public String prefix() {
    return StringUtils.isNotEmpty(methodAnnotation.prefix())
        ? methodAnnotation.prefix()
        : classAnnotation.prefix();
  }

  @Override
  public boolean logStackTrace() {
    return !methodAnnotation.logStackTrace()
        ? methodAnnotation.logStackTrace()
        : classAnnotation.logStackTrace();
  }

  @Override
  public boolean auditLog() {
    return !methodAnnotation.auditLog() ? methodAnnotation.auditLog() : classAnnotation.auditLog();
  }

  @Override
  public String[] excludeExceptions() {
    return ArrayUtils.isNotEmpty(methodAnnotation.excludeExceptions())
        ? methodAnnotation.excludeExceptions()
        : classAnnotation.excludeExceptions();
  }

  @Override
  public String[] exclude() {
    return ArrayUtils.isNotEmpty(methodAnnotation.exclude())
        ? methodAnnotation.exclude()
        : classAnnotation.exclude();
  }
}
