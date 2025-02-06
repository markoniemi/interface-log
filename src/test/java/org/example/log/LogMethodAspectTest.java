package org.example.log;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class,OutputCaptureExtension.class})
@Import(TestConfig.class)
@WithMockUser(username = "user")
class LogMethodAspectTest {
  @Autowired MethodAnnotationService methodAnnotationService;
  @Autowired ClassAnnotationService classAnnotationService;

  @Test
  void useDefaults(CapturedOutput output) {
    assertNotNull(methodAnnotationService.useDefaults());
    assertThat(output).contains("name | useDefaults | OK | "," | []");
  }

  @Test
  void excludeParameter(CapturedOutput output) throws Throwable {
    methodAnnotationService.excludeParameter(
        new User("username", "password", "email", Role.ROLE_USER));
    assertThat(output).contains("name | excludeParameter | OK | "," | []");
  }

  @Test
  void logParameterWithPrinter(CapturedOutput output) throws Throwable {
    methodAnnotationService.logParameterWithPrinter(
        new User("username", "password", "email", Role.ROLE_USER));
    assertThat(output).contains("name | logParameterWithPrinter | OK | "," | [user: username: username, ]");
  }

  @Test
  void returnPrimitive(CapturedOutput output) throws Throwable {
    methodAnnotationService.returnPrimitive();
    assertThat(output).contains("name | v1/returnPrimitive | OK | "," | []");
  }
  @Test
  void logNullParameter(CapturedOutput output) throws Throwable {
    methodAnnotationService.logNullParameter(null);
    assertThat(output).contains("name | logNullParameter | OK | "," | [user: null, ]");
  }

  @Test
  void useDifferentParameters(CapturedOutput output) throws Throwable {
    methodAnnotationService.useDifferentParameters(
        new User("username", "password", "email", Role.ROLE_USER), "string", new Date(), false);
    assertThat(output).contains("name | useDifferentParameters | OK | "," | [user: User(id=null");
  }

  @Test
  void useDifferentLogger(CapturedOutput output) throws Throwable {
    methodAnnotationService.useDifferentLogger();
    assertThat(output).contains("name | useDifferentLogger | OK | "," | []");
  }

  @Test
  void useLoggerFromSpel(CapturedOutput output) throws Throwable {
    methodAnnotationService.useLoggerFromSpel();
    assertThat(output).contains("name | useLoggerFromSpel | OK | "," | []");
  }

  @Test
  void throwException(CapturedOutput output) throws Throwable {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            methodAnnotationService.throwException(
                new User("username", "password", "email", Role.ROLE_USER)));
    assertThat(output).contains("name | throwException | IllegalArgumentException(update fails) | "," | [user: User(id=null, username=username, email=email, role=ROLE_USER), ]");
  }

  @Test
  @Disabled
  void throwAndLogException(CapturedOutput output) throws Throwable {
    assertThrows(RuntimeException.class, () -> methodAnnotationService.throwAndLogException());
    assertThat(output).contains("name | useDifferentParameters | OK | "," | []");
  }

  @Test
  void throwAndExcludeException(CapturedOutput output) throws Throwable {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            methodAnnotationService.throwAndExcludeException(
                new User("username", "password", "email", Role.ROLE_USER)));
    assertThat(output).contains("name | throwAndExcludeException | IllegalArgumentException(update fails) | "," | [user: User(id=null, username=username, email=email, role=ROLE_USER), ]");
  }
  
  @Test
  void useClassLevelAnnotation(CapturedOutput output) {
    classAnnotationService.useClassLevelAnnotation(new User("username", "password", "email", Role.ROLE_USER));
    assertThat(output).contains("interface log", "name | v1/useClassLevelAnnotation | OK | "," | [user: username: username, ]");
  }
  @Test
  void overrideLogName(CapturedOutput output) {
    classAnnotationService.overrideLogName();
    assertThat(output).contains("interface log", "name | v1/overrideLogName | OK | "," | []");
  }
}
