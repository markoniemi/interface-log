package org.example.log;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class InterfaceLogAspect {
  @Pointcut("@annotation(org.example.log.InterfaceLog)")
  public void interfaceLog() {}

  @Around(value = "@annotation(interfaceLog)", argNames = "interfaceLog")
  public Object adviceAround(ProceedingJoinPoint joinPoint, InterfaceLog interfaceLog)
      throws Throwable {
    long startTime = System.currentTimeMillis();
    try {
      Object returnValue = joinPoint.proceed(joinPoint.getArgs());
      logExecution(joinPoint, interfaceLog, startTime, null);
      return returnValue;
    } catch (Throwable e) {
      logExecution(joinPoint, interfaceLog, startTime, e);
      throw e;
    }
  }

  private void logExecution(JoinPoint joinPoint, InterfaceLog interfaceLog, long startTime,
      Throwable e) throws ReflectiveOperationException {
    interfaceLog = mergeAnnotations(joinPoint, interfaceLog);
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
    Method[] declaredMethods = joinPoint.getTarget().getClass().getDeclaredMethods();
    for (Method method : declaredMethods) {
      if (method.getName().equals(joinPoint.getSignature().getName())) {
        for (Class<?> exception : method.getExceptionTypes()) {
          if (exception.getCanonicalName().equals(e.getClass().getCanonicalName())) {
            return true;
          }
        }
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
      if (isLogged(parameterNames, interfaceLog.exclude())) {
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

  private boolean isLogged(String[] parameterNames, String[] skipParameters) {
    // skipParameters is never null
    // isLogged is not called if parameterNames is empty
    return Collections.disjoint(Set.of(parameterNames), Set.of(skipParameters));
  }

  private InterfaceLog mergeAnnotations(JoinPoint joinPoint, InterfaceLog methodAnnotation) {
    InterfaceLog classAnnotation =
        joinPoint.getTarget().getClass().getAnnotation(InterfaceLog.class);
    if (classAnnotation == null) {
      return methodAnnotation;
    }
    return new InterfaceLogMapper(classAnnotation, methodAnnotation);
  }
}
