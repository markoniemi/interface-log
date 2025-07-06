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
  public String prefix() {
    return StringUtils.isNotBlank(methodAnnotation.prefix())
        ? methodAnnotation.prefix()
        : classAnnotation.prefix();
  }

  @Override
  public boolean stackTrace() {
    return !methodAnnotation.stackTrace()
        ? methodAnnotation.stackTrace()
        : classAnnotation.stackTrace();
  }

  @Override
  public String[] exclude() {
    return ArrayUtils.isNotEmpty(methodAnnotation.exclude())
        ? methodAnnotation.exclude()
        : classAnnotation.exclude();
  }
}
