package com.jide.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author 晓蝈
 * @version 1.0
 */
@Configuration
public class SpringMvcSupport extends WebMvcConfigurationSupport {
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        //当访问 /pages/??? 的时候不要走mvc  而是走 pages 目录下的内容
        registry.addResourceHandler("/pages/**").addResourceLocations("/pages/");
    }
}
