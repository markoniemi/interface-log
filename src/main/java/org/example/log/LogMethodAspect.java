package org.example.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogMethodAspect {
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

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

  private void logExecution(JoinPoint joinPoint, LogMethod logMethod, long startTime, Throwable e) throws ReflectiveOperationException {
    Logger log = getLog(joinPoint, logMethod);
    String className = joinPoint.getTarget().getClass().getSimpleName();
    String methodName = joinPoint.getSignature().getName();
    String parameters = getParameters(joinPoint, logMethod);
    if (e == null) {
      log.info("{}.{} | OK | {}ms | {}", className, methodName,
          System.currentTimeMillis() - startTime, parameters);
    } else {
      if (logMethod.logStackTrace() && !skipException(logMethod, e)) {
        log.warn("{}.{} | {} | {}ms | {}", className, methodName,e.getClass().getSimpleName(),
            System.currentTimeMillis() - startTime, parameters, e);
      } else {
        log.warn("{}.{} | {} | {}ms | {}", className, methodName,e.getClass().getSimpleName(),
            System.currentTimeMillis() - startTime, parameters);
      }
    }
  }

  private boolean skipException(LogMethod logMethod, Throwable e) {
    for (String exceptionName : logMethod.excludeExceptions()) {
      if (e.getClass().getSimpleName().equals(exceptionName)) {
        return true;
      }
    }
    return false;
  }

  private Logger getLog(JoinPoint joinPoint, LogMethod logMethod) {
    String logName = StringUtils.isNotEmpty(logMethod.logName()) ? logMethod.logName()
        : joinPoint.getTarget().getClass().getCanonicalName();
    return LogManager.getLogger(logName);
  }

  private String getParameters(JoinPoint joinPoint, LogMethod logMethod) throws ReflectiveOperationException {
    ParameterPrinter printer =null; 
    if (StringUtils.isNotEmpty( logMethod.printer())){
          printer = (ParameterPrinter) Class.forName(logMethod.printer()).getDeclaredConstructor().newInstance();
    }
    String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
    Object[] parameters = joinPoint.getArgs();
    StringBuilder parameterString = new StringBuilder();
    for (int i = 0; i < parameters.length; i++) {
      if (isLogged(parameterNames, logMethod.exclude())) {
        parameterString.append(printParameter(parameters[i],printer));
        parameterString.append(", ");
      }
    }
    return parameterString.toString();
  }

  private String printParameter(Object parameter,ParameterPrinter printer) {
    if (printer!=null) {
      return printer.print(parameter);
    }
    if (parameter instanceof Date date) {
      return DATE_FORMAT.format(date);
    } else {
      return parameter.toString();
    }
  }

  private boolean isLogged(String[] parameterNames, String[] skipArgs) {
    return true;
  }
}
