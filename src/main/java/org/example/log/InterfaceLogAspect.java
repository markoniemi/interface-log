package org.example.log;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class InterfaceLogAspect {
  @Around("@annotation(org.example.log.InterfaceLog) || @within(org.example.log.InterfaceLog)")
  public Object adviceAround(ProceedingJoinPoint joinPoint) throws Throwable {
    long startTime = System.currentTimeMillis();
    try {
      Object returnValue = joinPoint.proceed(joinPoint.getArgs());
      logExecution(joinPoint, startTime, null);
      return returnValue;
    } catch (Throwable e) {
      logExecution(joinPoint, startTime, e);
      throw e;
    }
  }

  private void logExecution(JoinPoint joinPoint, long startTime, Throwable e)
      throws ReflectiveOperationException {
    InterfaceLog interfaceLog = mergeAnnotations(joinPoint);
    log(joinPoint.getTarget().getClass().getCanonicalName(), getLevel(joinPoint, e),
        getException(interfaceLog, joinPoint, e), "{}{} | {} | {}ms | {} | {}",
        interfaceLog.prefix(), joinPoint.getSignature().getName(), e == null,
        System.currentTimeMillis() - startTime, printParameters(joinPoint, interfaceLog),
        printException(interfaceLog, joinPoint, e));
  }

  private String printException(InterfaceLog interfaceLog, JoinPoint joinPoint, Throwable e) {
    return e != null ? String.format("%s(%s)", e.getClass().getCanonicalName(), e.getMessage())
        : "";
  }

  private void log(String className, Level level, Throwable cause, String template, String prefix,
      String method, boolean success, Long time, String parameters, String exception) {
    String result = success ? "OK" : "FAIL";
    LoggerFactory.getLogger(className).atLevel(level).setCause(cause).log(template, prefix, method,
        result, time, parameters, exception);
  }

  private Level getLevel(JoinPoint joinPoint, Throwable e) {
    return e != null && !isExpectedException(joinPoint, e) ? Level.WARN : Level.INFO;
  }

  private boolean isExpectedException(JoinPoint joinPoint, Throwable e) {
    Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
    for (Class<?> exception : method.getExceptionTypes()) {
      if (exception.isAssignableFrom(e.getClass())) {
        return true;
      }
    }
    return false;
  }

  private Throwable getException(InterfaceLog interfaceLog, JoinPoint joinPoint, Throwable e) {
    return e != null && interfaceLog.stackTrace() && !isExpectedException(joinPoint, e) ? e : null;
  }

  private String printParameters(JoinPoint joinPoint, InterfaceLog interfaceLog)
      throws ReflectiveOperationException {
    String[] parameterNames = getParameterNames((MethodSignature) joinPoint.getSignature());
    Object[] parameters = joinPoint.getArgs();
    StringBuilder parameterString = new StringBuilder("[");
    for (int i = 0; i < parameters.length; i++) {
      if (!ArrayUtils.contains(interfaceLog.exclude(), parameterNames[i])) {
        parameterString.append(String.format("%s: %s, ", parameterNames[i], parameters[i]));
      }
    }
    return parameterString.append("]").toString();
  }

  private String[] getParameterNames(MethodSignature signature) {
    // signature.getParameterNames does not work on proxies
    // https://stackoverflow.com/questions/25226441/java-aop-joinpoint-does-not-get-parameter-names
    // return signature.getParameterNames();
    List<String> parameterNames = new ArrayList<>();
    for (Parameter parameter : signature.getMethod().getParameters()) {
      parameterNames.add(parameter.getName());
    }
    return parameterNames.toArray(new String[0]);
  }

  private InterfaceLog mergeAnnotations(JoinPoint joinPoint) {
    // the pointcut matches a method annotation, a class annotation or both
    InterfaceLog methodAnnotation = AnnotationUtils.findAnnotation(
        ((MethodSignature) joinPoint.getSignature()).getMethod(), InterfaceLog.class);
    InterfaceLog classAnnotation =
        AnnotationUtils.findAnnotation(joinPoint.getTarget().getClass(), InterfaceLog.class);
    if (methodAnnotation == null) {
      return classAnnotation;
    }
    if (classAnnotation == null) {
      return methodAnnotation;
    }
    return new InterfaceLogMapper(classAnnotation, methodAnnotation);
  }
}
