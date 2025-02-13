package org.example.log;

import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import lombok.extern.log4j.Log4j2;

@Service
//@Log4j2
public class MethodAnnotationService {
  public static Logger log = LogManager.getLogger(MethodAnnotationService.class);
  @InterfaceLog
  public User[] useDefaults() {
    User[] users = {new User("username", "password", "email", Role.ROLE_USER)};
    return users;
  }

  @InterfaceLog(exclude = "user")
  public User excludeParameter(User user) {
    log.debug("returnClass");
    return null;
  }

  @InterfaceLog(printer = "org.example.log.UserPrinter")
  public void logParameterWithPrinter(User user) {
    return;
  }

  @InterfaceLog(prefix="v1/")
  public int returnPrimitive() {
    return 0;
  }
  @InterfaceLog
  public void logNullParameter(User user) {
  }

  @InterfaceLog(logStackTrace = false)
  public User throwException(User user) {
    throw new IllegalArgumentException("update fails");
  }

  @InterfaceLog(excludeExceptions = {"IllegalArgumentException"})
  public User throwAndExcludeException(User user) {
    throw new IllegalArgumentException("update fails");
  }

  @InterfaceLog
  public void throwAndLogException() {
    throw new RuntimeException("update fails");
  }

  @InterfaceLog
  public User useDifferentParameters(User user, String username, Date date, boolean bool) {
    return null;
  }

  @InterfaceLog(logName = "org.example.api.user.TestServiceImpl")
  public User useDifferentLogger() {
    return null;
  }
  @InterfaceLog(logName = "#this.log")
  public User useLoggerFromSpel( ) {
    return null;
  }
}
