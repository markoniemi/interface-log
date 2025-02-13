package org.example.log;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Date;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, OutputCaptureExtension.class})
@Import(TestConfig.class)
@WithMockUser(username = "user")
class InterfaceLogAspectTest {
  @Autowired MethodAnnotationService methodAnnotationService;
  @Autowired ClassAnnotationService classAnnotationService;

  @Test
  void useDefaults(CapturedOutput output) {
    assertNotNull(methodAnnotationService.useDefaults());
    assertThat(output).contains("useDefaults | OK | applicationName | user | ", " | []");
  }

  @Test
  void excludeParameter(CapturedOutput output) throws Throwable {
    methodAnnotationService.excludeParameter(
        new User("username", "password", "email", Role.ROLE_USER));
    assertThat(output).contains("excludeParameter | OK | applicationName | user | ", " | []");
  }

  @Test
  void logParameterWithPrinter(CapturedOutput output) throws Throwable {
    methodAnnotationService.logParameterWithPrinter(
        new User("username", "password", "email", Role.ROLE_USER));
    assertThat(output)
        .contains(
            "logParameterWithPrinter | OK | applicationName | user | ",
            " | [user: username: username, ]");
  }

  @Test
  void returnPrimitive(CapturedOutput output) throws Throwable {
    methodAnnotationService.returnPrimitive();
    assertThat(output).contains("v1/returnPrimitive | OK | applicationName | user | ", " | []");
  }

  @Test
  void logNullParameter(CapturedOutput output) throws Throwable {
    methodAnnotationService.logNullParameter(null);
    assertThat(output)
        .contains("logNullParameter | OK | applicationName | user | ", " | [user: null, ]");
  }

  @Test
  void useDifferentParameters(CapturedOutput output) throws Throwable {
    methodAnnotationService.useDifferentParameters(
        new User("username", "password", "email", Role.ROLE_USER), "string", new Date(), false);
    assertThat(output)
        .contains(
            "useDifferentParameters | OK | applicationName | user | ", " | [user: User(id=null");
  }

  @Test
  void auditLog(CapturedOutput output) throws Throwable {
    methodAnnotationService.auditLog();
    assertThat(output).contains("interface", "auditLog | OK | applicationName | user | ", " | []");
    assertThat(output).contains("audit", "auditLog | OK | applicationName | user | ", " | []");
  }

  @Test
  void throwException(CapturedOutput output) throws Throwable {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            methodAnnotationService.throwException(
                new User("username", "password", "email", Role.ROLE_USER)));
    assertThat(output)
        .contains(
            "throwException | FAIL(IllegalArgumentException) | applicationName | user | ",
            " | [user: User(id=null, username=username, email=email, role=ROLE_USER), ]");
  }

  @Test
  @Disabled
  void throwAndLogException(CapturedOutput output) throws Throwable {
    assertThrows(RuntimeException.class, () -> methodAnnotationService.throwAndLogException());
    assertThat(output).contains("name | useDifferentParameters | OK | ", " | []");
  }

  @Test
  void throwAndExcludeException(CapturedOutput output) throws Throwable {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            methodAnnotationService.throwAndExcludeException(
                new User("username", "password", "email", Role.ROLE_USER)));
    assertThat(output)
        .contains(
            "throwAndExcludeException | FAIL(IllegalArgumentException) | applicationName | user | ",
            " | [user: User(id=null, username=username, email=email, role=ROLE_USER), ]");
  }

  @Test
  void useClassLevelAnnotation(CapturedOutput output) {
    classAnnotationService.useClassLevelAnnotation(
        new User("username", "password", "email", Role.ROLE_USER));
    assertThat(output)
        .contains(
            "interface",
            "v1/useClassLevelAnnotation | OK | applicationName | user | ",
            " | [user: username: username, ]");
  }
}
