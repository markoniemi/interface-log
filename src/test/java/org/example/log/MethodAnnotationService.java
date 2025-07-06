package org.example.log;

import java.util.Date;
import org.springframework.stereotype.Service;

@Service
public class MethodAnnotationService {
  @InterfaceLog
  public User[] useDefaults() {
    User[] users = {new User("username", "password", "email", Role.ROLE_USER)};
    return users;
  }

  @InterfaceLog(exclude = {"user", "anotherParameter"})
  public User skipParameters(User user, boolean anotherParameter) {
    return null;
  }

  @InterfaceLog(prefix = "v1/")
  public int returnPrimitive() {
    return 0;
  }

  @InterfaceLog
  public void logNullParameter(User user) {}

  @InterfaceLog(stackTrace = false)
  public User throwException(User user) {
    throw new IllegalArgumentException("update fails");
  }

  @InterfaceLog
  public User useDifferentParameters(User user, String username, Date date, boolean bool) {
    return null;
  }
}
