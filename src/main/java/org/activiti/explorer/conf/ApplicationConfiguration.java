package org.activiti.explorer.conf;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ComponentScan(basePackages = { "org.activiti.explorer.conf" })
@ImportResource({"classpath:activiti-ui-context.xml"})
public class ApplicationConfiguration {
  
}
