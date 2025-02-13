package org.example.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface InterfaceLog {
  String[] exclude() default "";
  boolean logStackTrace() default true;
  String[] excludeExceptions() default "";
  String logName() default "";
  String printer() default "";
  String prefix() default "";
}