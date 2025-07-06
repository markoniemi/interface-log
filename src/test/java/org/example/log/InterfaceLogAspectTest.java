package org.example.log;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, OutputCaptureExtension.class})
@ComponentScan(basePackageClasses = {MethodAnnotationService.class, ClassAnnotationService.class})
@EnableAspectJAutoProxy
class InterfaceLogAspectTest {
  @Autowired MethodAnnotationService methodAnnotationService;
  @Autowired ClassAnnotationService classAnnotationService;

  @Test
  void useDefaults(CapturedOutput output) {
    assertNotNull(methodAnnotationService.useDefaults());
    assertThat(output).contains("useDefaults | OK | ", " | []");
  }

  @Test
  void skipParameters(CapturedOutput output) throws Throwable {
    methodAnnotationService.skipParameters(
        new User("username", "password", "email", Role.ROLE_USER), true);
    assertThat(output).contains("skipParameters | OK | ", " | []");
  }

  @Test
  void returnPrimitive(CapturedOutput output) throws Throwable {
    methodAnnotationService.returnPrimitive();
    assertThat(output).contains("v1/returnPrimitive | OK | ", " | []");
  }

  @Test
  void logNullParameter(CapturedOutput output) throws Throwable {
    methodAnnotationService.logNullParameter(null);
    assertThat(output).contains("logNullParameter | OK | ", " | [user: null, ]");
  }

  @Test
  void useDifferentParameters(CapturedOutput output) throws Throwable {
    methodAnnotationService.useDifferentParameters(
        new User("username", "password", "email", Role.ROLE_USER), "string", new Date(), false);
    assertThat(output).contains("useDifferentParameters | OK | ", " | [user: User(id=null");
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
            "throwException | FAIL | ",
            " | [user: User(id=null, username=username, email=email, role=ROLE_USER), ] | ",
            "IllegalArgumentException(update fails)");
  }

  @Test
  void logExpectedException(CapturedOutput output) {
    assertThrows(
        IllegalArgumentException.class, () -> classAnnotationService.logExpectedException());
    assertThat(output)
        .contains("ClassAnnotationService", "INFO", "v1/logExpectedException | FAIL | ", " | []");
  }

  @Test
  void logUnexpectedException(CapturedOutput output) {
    assertThrows(NullPointerException.class, () -> classAnnotationService.logUnexpectedException());
    assertThat(output)
        .contains(
            "ClassAnnotationService",
            "WARN",
            "v1/logUnexpectedException | FAIL | ",
            " | [] | ",
            "NullPointerException(unexpected exception)");
  }
}
