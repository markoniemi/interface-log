package org.example.log;

import org.springframework.stereotype.Service;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@InterfaceLog(prefix = "v1/", logStackTrace = true, logName="interface log", printer = "org.example.log.UserPrinter")
public class ClassAnnotationService {
  @InterfaceLog
  public void useClassLevelAnnotation(User user) {
    log.debug("useClassLevelAnnotation");
  }

  @InterfaceLog(logStackTrace = false)
  public void overrideLogName() {
    log.debug("overrideLogName");
  }
  @InterfaceLog(logStackTrace = false)
  public void overrideLogStackTrace() {
    throw new IllegalArgumentException("update fails");
  }
  @InterfaceLog
  public void useClassLevelPrinter() {
    log.debug("useClassLevelPrinter");
  }
}
