package org.example.log;

import org.example.log.LogMethodAspect;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(basePackageClasses = {LogMethodAspect.class,TestService.class})
@EnableAspectJAutoProxy
public class TestConfig {
}
