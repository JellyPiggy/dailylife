package com.jide.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author 晓蝈
 * @version 1.0
 */
@Configuration
@ComponentScan({"com.jide.dao", "com.jide.service"})
public class SpringConfig {
}
