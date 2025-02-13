package org.example.log;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(basePackageClasses = {InterfaceLogAspect.class,MethodAnnotationService.class,ClassAnnotationService.class})
@EnableAspectJAutoProxy
public class TestConfig {
}
