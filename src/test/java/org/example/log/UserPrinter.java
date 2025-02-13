package org.example.log;

public class UserPrinter implements ParameterPrinter {

  @Override
  public String print(Object target) {
    if (target instanceof User user) {
      return "username: " + user.getUsername();
    }
    return "";
  }
}
