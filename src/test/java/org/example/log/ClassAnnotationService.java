package org.example.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@InterfaceLog(prefix = "v1/", stackTrace = true)
public class ClassAnnotationService {
  @InterfaceLog
  public void logExpectedException() throws IllegalArgumentException {
    throw new IllegalArgumentException("expected exception");
  }

  @InterfaceLog(stackTrace = false)
  public void logUnexpectedException() throws IllegalArgumentException {
    log.debug("stackTrace disabled");
    throw new NullPointerException("unexpected exception");
  }
}
