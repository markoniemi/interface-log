package org.example.log;

import java.util.Collections;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class InterfaceLogAspect {
  @Pointcut("@annotation(org.example.log.InterfaceLog)")
  public void interfaceLog() {}

  @Around(value = "@annotation(interfaceLog)", argNames = "interfaceLog")
  public Object adviceAround(ProceedingJoinPoint joinPoint, InterfaceLog interfaceLog) throws Throwable {
    long startTime = System.currentTimeMillis();
    Object result = null;
    try {
      result = joinPoint.proceed(joinPoint.getArgs());
    } catch (Throwable e) {
      logExecution(joinPoint, interfaceLog, startTime, e);
      throw e;
    }
    logExecution(joinPoint, interfaceLog, startTime, null);
    return result;
  }

  private void logExecution(JoinPoint joinPoint, InterfaceLog interfaceLog, long startTime, Throwable e)
      throws ReflectiveOperationException {
    interfaceLog = mergeAnnotations(joinPoint, interfaceLog);
    getLog(joinPoint, interfaceLog)
        .atLevel(e == null ? Level.INFO : Level.WARN)
        .setCause(printStackTrace(interfaceLog, e))
        .log(
            "{}{} | {} | {} | {} | {}ms | {}",
            interfaceLog.prefix(),
            joinPoint.getSignature().getName(),
            getResult(e),
            getApplicationName(),
            SecurityContextHolder.getContext().getAuthentication().getName(),
            System.currentTimeMillis() - startTime,
            printParameters(joinPoint, interfaceLog));
  }

  private Logger getLog(JoinPoint joinPoint, InterfaceLog interfaceLog) {
    if (StringUtils.isEmpty(interfaceLog.logName())) {
      return LoggerFactory.getLogger(joinPoint.getTarget().getClass().getCanonicalName());
    }
    return LoggerFactory.getLogger(interfaceLog.logName());
  }

  private Throwable printStackTrace(InterfaceLog interfaceLog, Throwable e) {
    return e != null && (!interfaceLog.logStackTrace() || skipException(interfaceLog, e)) ? null : e;
  }

  private String getResult(Throwable e) {
    return e == null ? "OK" : "FAIL(" + e.getClass().getSimpleName() + ")";
  }

  private String getApplicationName() {
    return "applicationName";
  }

  private boolean skipException(InterfaceLog interfaceLog, Throwable e) {
    return Set.of(interfaceLog.excludeExceptions()).contains(e.getClass().getSimpleName());
  }

  private String printParameters(JoinPoint joinPoint, InterfaceLog interfaceLog)
      throws ReflectiveOperationException {
    String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
    Object[] parameters = joinPoint.getArgs();
    StringBuilder parameterString = new StringBuilder("[");
    for (int i = 0; i < parameters.length; i++) {
      if (isLogged(parameterNames, interfaceLog.exclude())) {
        parameterString
            .append(parameterNames[i])
            .append(": ")
            .append(printParameter(parameters[i], getPrinter(interfaceLog)))
            .append(", ");
      }
    }
    return parameterString.append("]").toString();
  }

  private ParameterPrinter getPrinter(InterfaceLog interfaceLog) throws ReflectiveOperationException {
    if (StringUtils.isNotEmpty(interfaceLog.printer())) {
      return (ParameterPrinter)
          Class.forName(interfaceLog.printer()).getDeclaredConstructor().newInstance();
    }
    return null;
  }

  private String printParameter(Object parameter, ParameterPrinter printer) {
    if (parameter == null) {
      return "null";
    }
    return printer != null ? printer.print(parameter) : parameter.toString();
  }

  private boolean isLogged(String[] parameterNames, String[] skipArgs) {
    return Collections.disjoint(Set.of(parameterNames), Set.of(skipArgs));
  }

  private InterfaceLog mergeAnnotations(JoinPoint joinPoint, InterfaceLog methodAnnotation) {
    InterfaceLog classAnnotation =
        (InterfaceLog)
            ((MethodSignature) joinPoint.getSignature())
                .getDeclaringType()
                .getAnnotation(InterfaceLog.class);
    if (classAnnotation == null) {
      return methodAnnotation;
    }
    return new InterfaceLogMapper(classAnnotation, methodAnnotation);
  }
}
