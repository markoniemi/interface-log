package org.example.log;

import java.util.Collections;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.FormattedMessage;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogMethodAspect {
  private ExpressionParser expressionParser = new SpelExpressionParser();

  @Pointcut("@annotation(org.example.log.LogMethod)")
  public void logMethod() {}

  @Around(value = "@annotation(logMethod)", argNames = "logMethod")
  public Object adviceAround(ProceedingJoinPoint joinPoint, LogMethod logMethod) throws Throwable {
    long startTime = System.currentTimeMillis();
    Object result = null;
    try {
      result = joinPoint.proceed(joinPoint.getArgs());
    } catch (Throwable e) {
      logExecution(joinPoint, logMethod, startTime, e);
      throw e;
    }
    logExecution(joinPoint, logMethod, startTime, null);
    return result;
  }

  private void logExecution(JoinPoint joinPoint, LogMethod logMethod, long startTime, Throwable e)
      throws ReflectiveOperationException {
    String user = SecurityContextHolder.getContext().getAuthentication().getName();
    logMethod = mergeAnnotations(joinPoint, logMethod);
    Logger log = getLog(joinPoint, logMethod);
    String methodName = joinPoint.getSignature().getName();
    String parameterString = printParameters(joinPoint, logMethod);
    final String successTemplate="{}{} | {} | {} | {} | {}ms | {}";
    final String errorTemplate="{}{} | {}({}) | {} | {} | {}ms | {}";
    String result=e==null?"OK":"FAIL";
    if (e == null) {
      log.info(
          successTemplate,
          logMethod.prefix(),
          methodName,
          result,
          "applicationName",
          user,
          System.currentTimeMillis() - startTime,
          parameterString);
    } else {
      if (logMethod.logStackTrace() && !skipException(logMethod, e)) {
        log.warn(
            errorTemplate,
            logMethod.prefix(),
            methodName,
            result,
            e.getClass().getSimpleName(),
            "applicationName",
            user,
            System.currentTimeMillis() - startTime,
            parameterString,
            e);
      } else {
        log.warn(
            errorTemplate,
            logMethod.prefix(),
            methodName,
            result,
            e.getClass().getSimpleName(),
            "applicationName",
            user,
            System.currentTimeMillis() - startTime,
            parameterString);
      }
    }
  }

  private boolean skipException(LogMethod logMethod, Throwable e) {
    return Set.of(logMethod.excludeExceptions()).contains(e.getClass().getSimpleName());
  }

  private Logger getLog(JoinPoint joinPoint, LogMethod logMethod) {
    if (StringUtils.isEmpty(logMethod.logName())) {
      return LogManager.getLogger(joinPoint.getTarget().getClass().getCanonicalName());
    }
    if (logMethod.logName().startsWith("#")) {
      return getLogObject(joinPoint, logMethod);
    }
    return LogManager.getLogger(logMethod.logName());
  }

  private Logger getLogObject(JoinPoint joinPoint, LogMethod logMethod) {
    return expressionParser
        .parseExpression(logMethod.logName())
        .getValue(joinPoint.getTarget(), Logger.class);
  }

  private String printParameters(JoinPoint joinPoint, LogMethod logMethod)
      throws ReflectiveOperationException {
    String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
    Object[] parameters = joinPoint.getArgs();
    StringBuilder parameterString = new StringBuilder("[");
    for (int i = 0; i < parameters.length; i++) {
      if (isLogged(parameterNames, logMethod.exclude())) {
        parameterString
            .append(parameterNames[i])
            .append(": ")
            .append(printParameter(parameters[i], getPrinter(logMethod)))
            .append(", ");
      }
    }
    return parameterString.append("]").toString();
  }

  private ParameterPrinter getPrinter(LogMethod logMethod) throws ReflectiveOperationException {
    if (StringUtils.isNotEmpty(logMethod.printer())) {
      return (ParameterPrinter)
          Class.forName(logMethod.printer()).getDeclaredConstructor().newInstance();
    }
    return null;
  }

  private String printParameter(Object parameter, ParameterPrinter printer) {
    if (parameter == null) {
      return "null";
    }
    if (printer != null) {
      return printer.print(parameter);
    }
    return parameter.toString();
  }

  private boolean isLogged(String[] parameterNames, String[] skipArgs) {
    return Collections.disjoint(Set.of(parameterNames), Set.of(skipArgs));
  }

  private LogMethod mergeAnnotations(JoinPoint joinPoint, LogMethod logMethod) {
    LogMethod classLogMethod =
        (LogMethod)
            ((MethodSignature) joinPoint.getSignature())
                .getDeclaringType()
                .getAnnotation(LogMethod.class);
    if (classLogMethod == null) {
      return logMethod;
    }
    return new LogMethodMapper(classLogMethod, logMethod);
  }
}
