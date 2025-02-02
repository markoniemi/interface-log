package org.example.log;

import org.springframework.stereotype.Service;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@LogMethod(prefix = "v1/",logStackTrace = false)
public class ClassAnnotationService {
  @LogMethod
  public User[] returnList() {
    User[] users = {new User("username", "password", "email", Role.ROLE_USER)};
    return users;
  }

  @LogMethod(logStackTrace = true)
  public void overrideLogStackTrace() {
    log.debug("returnClass");
  }
  @LogMethod(exclude = "user")
  public User excludeParameter(User user) {
    log.debug("returnClass");
    return null;
  }
}
