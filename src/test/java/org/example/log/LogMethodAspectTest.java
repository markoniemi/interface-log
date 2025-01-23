package org.example.log;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import(TestConfig.class)
class LogMethodAspectTest {
  @Autowired
  TestService testService;
  @Spy
  Logger log = LogManager.getLogger("test");
  private MockedStatic<LogManager> logManager;

  @Test
  void returnList() {
    assertNotNull(testService.returnList());
  }

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
  void returnClass() throws Throwable {
    testService.returnClass(new User("username", "password", "email", Role.ROLE_USER));
    verify(log).info(eq("{}.{} | OK | {}ms | {}"), eq("TestService"), eq("returnClass"),
        anyLong(), anyString());
  }
  @Test
  void logParameterWithPrinter() throws Throwable {
    testService.logParameterWithPrinter(new User("username", "password", "email", Role.ROLE_USER));
    verify(log).info(eq("{}.{} | OK | {}ms | {}"), eq("TestService"), eq("logParameterWithPrinter"),
        anyLong(), anyString());
  }

  @Test
  void returnPrimitive() throws Throwable {
    testService.returnPrimitive();
    verify(log).info(eq("{}.{} | OK | {}ms | {}"), eq("TestService"), eq("returnPrimitive"),
        anyLong(), anyString());
  }

  @Test
  void useDifferentParameters() throws Throwable {
    testService.useDifferentParameters(new User("username", "password", "email", Role.ROLE_USER),
        "string", new Date(), false);
    verify(log).info(eq("{}.{} | OK | {}ms | {}"), eq("TestService"), eq("useDifferentParameters"),
        anyLong(), anyString());
  }

  @Test
  void useDifferentLogger() throws Throwable {
    testService.useDifferentLogger();
    verify(log).info(eq("{}.{} | OK | {}ms | {}"), eq("TestService"), eq("useDifferentLogger"),
        anyLong(), anyString());
  }

  @Test
  void throwException() throws Throwable {
    assertThrows(IllegalArgumentException.class, () -> testService
        .throwException(new User("username", "password", "email", Role.ROLE_USER)));
    verify(log).warn(eq("{}.{} | {} | {}ms | {}"), eq("TestService"), eq("throwException"),eq("IllegalArgumentException"),
        anyLong(), anyString());
  }

  @Test
  @Disabled
  void throwAndLogException() throws Throwable {
    assertThrows(RuntimeException.class, () -> testService.throwAndLogException());
    verify(log).warn(eq("{}.{} | {} | {}ms | {}"), eq("TestService"),
        eq("throwAndLogException"), anyLong(), anyString(), any(Exception.class));
  }
  @Test
  void throwAndExcludeException() throws Throwable {
    assertThrows(IllegalArgumentException.class, () -> testService
        .throwAndExcludeException(new User("username", "password", "email", Role.ROLE_USER)));
    verify(log).warn(eq("{}.{} | {} | {}ms | {}"), eq("TestService"), eq("throwAndExcludeException"),eq("IllegalArgumentException"),
        anyLong(), anyString());
  }
}
