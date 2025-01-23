package org.example.log;

import java.util.Date;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class TestService {
  @LogMethod
  public User[] returnList() {
    User[] users = {new User("username", "password", "email", Role.ROLE_USER)};
    return users;
  }

  @LogMethod(exclude = "user")
  public User excludeParameter(User user) {
    log.debug("returnClass");
    return null;
  }

  @LogMethod(printer = "org.example.log.UserPrinter")
  public void logParameterWithPrinter(User user) {
    return;
  }

  @LogMethod
  public int returnPrimitive() {
    return 0;
  }

  @LogMethod(logStackTrace = false)
  public User throwException(User user) {
    throw new IllegalArgumentException("update fails");
  }

  @LogMethod(excludeExceptions = {"IllegalArgumentException"})
  public User throwAndExcludeException(User user) {
    throw new IllegalArgumentException("update fails");
  }

  @LogMethod
  public void throwAndLogException() {
    throw new RuntimeException("update fails");
  }

  @LogMethod
  public User useDifferentParameters(User user, String username, Date date, boolean bool) {
    return null;
  }

  @LogMethod(logName = "org.example.api.user.TestServiceImpl")
  public User useDifferentLogger() {
    return null;
  }
}
