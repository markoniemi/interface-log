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
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class,OutputCaptureExtension.class})
@Import(TestConfig.class)
class LogMethodAspectTest {
  @Autowired MethodAnnotationService methodAnnotationService;
  @Autowired ClassAnnotationService classAnnotationService;
  @Spy Logger log = LogManager.getLogger("logSpy");
  private MockedStatic<LogManager> logManager;

  @BeforeEach
  void setUp() {
    logManager = Mockito.mockStatic(LogManager.class);
    logManager.when(() -> LogManager.getLogger(anyString())).thenReturn(log);
  }

  @AfterEach
  void tearDown() {
    logManager.close();
  }

  @Test
  void useDefaults(CapturedOutput output) {
    assertNotNull(methodAnnotationService.useDefaults());
    assertThat(output).contains("name | useDefaults | OK | "," | []");
  }

  @Test
  void excludeParameter() throws Throwable {
    methodAnnotationService.excludeParameter(
        new User("username", "password", "email", Role.ROLE_USER));
    verify(log)
        .info(
            eq("{} | {}{} | OK | {}ms | {}"),
            eq("name"),
            eq(""),
            eq("excludeParameter"),
            anyLong(),
            anyString());
  }

  @Test
  void logParameterWithPrinter() throws Throwable {
    methodAnnotationService.logParameterWithPrinter(
        new User("username", "password", "email", Role.ROLE_USER));
    verify(log)
        .info(
            eq("{} | {}{} | OK | {}ms | {}"),
            eq("name"),
            eq(""),
            eq("logParameterWithPrinter"),
            anyLong(),
            anyString());
  }

  @Test
  void returnPrimitive() throws Throwable {
    methodAnnotationService.returnPrimitive();
    verify(log)
        .info(
            eq("{} | {}{} | OK | {}ms | {}"),
            eq("name"),
            eq("v1/"),
            eq("returnPrimitive"),
            anyLong(),
            anyString());
  }

  @Test
  void useDifferentParameters() throws Throwable {
    methodAnnotationService.useDifferentParameters(
        new User("username", "password", "email", Role.ROLE_USER), "string", new Date(), false);
    verify(log)
        .info(
            eq("{} | {}{} | OK | {}ms | {}"),
            eq("name"),
            eq(""),
            eq("useDifferentParameters"),
            anyLong(),
            anyString());
  }

  @Test
  void useDifferentLogger() throws Throwable {
    methodAnnotationService.useDifferentLogger();
    verify(log)
        .info(
            eq("{} | {}{} | OK | {}ms | {}"),
            eq("name"),
            eq(""),
            eq("useDifferentLogger"),
            anyLong(),
            anyString());
  }

  @Test
  void useLoggerFromSpel(CapturedOutput output) throws Throwable {
    methodAnnotationService.useLoggerFromSpel();
    assertThat(output).contains("name | useLoggerFromSpel | OK | "," | []");
  }

  @Test
  void throwException() throws Throwable {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            methodAnnotationService.throwException(
                new User("username", "password", "email", Role.ROLE_USER)));
    verify(log)
        .warn(
            eq("{} | {}{} | {}({}) | {}ms | {}"),
            eq("name"),
            eq(""),
            eq("throwException"),
            eq("IllegalArgumentException"),
            anyString(),
            anyLong(),
            anyString());
  }

  @Test
  @Disabled
  void throwAndLogException() throws Throwable {
    assertThrows(RuntimeException.class, () -> methodAnnotationService.throwAndLogException());
    verify(log)
        .warn(
            eq("{} | {}{} | {}({}) | {}ms | {}"),
            eq("name"),
            eq(""),
            eq("throwAndLogException"),
            anyString(),
            anyLong(),
            anyString(),
            any(Exception.class));
  }

  @Test
  void throwAndExcludeException() throws Throwable {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            methodAnnotationService.throwAndExcludeException(
                new User("username", "password", "email", Role.ROLE_USER)));
    verify(log)
        .warn(
            eq("{} | {}{} | {}({}) | {}ms | {}"),
            eq("name"),
            eq(""),
            eq("throwAndExcludeException"),
            eq("IllegalArgumentException"),
            anyString(),
            anyLong(),
            anyString());
  }
}
