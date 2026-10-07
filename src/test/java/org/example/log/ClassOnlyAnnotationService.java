package org.example.log;

import org.springframework.stereotype.Service;

@Service
@InterfaceLog(prefix = "class/", exclude = "password")
public class ClassOnlyAnnotationService {
  public String noMethodAnnotation(String username, String password) {
    return username;
  }
}
