package org.example.log;

import java.util.Date;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MethodAnnotationService {
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

  @InterfaceLog(prefix = "v1/")
  public int returnPrimitive() {
    return 0;
  }

  @InterfaceLog
  public void logNullParameter(User user) {}

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

  @InterfaceLog(auditLog = true)
  public User auditLog() {
    return null;
  }
}
