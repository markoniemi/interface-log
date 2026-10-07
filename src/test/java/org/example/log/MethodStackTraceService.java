package org.example.log;

import org.springframework.stereotype.Service;

@Service
@InterfaceLog
public class MethodStackTraceService {
  @InterfaceLog(stackTrace = true)
  public void logStackTrace() {
    throw new NullPointerException("stack trace enabled on method");
  }
}
