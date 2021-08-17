package com.example.sharedTooling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

/**
 * Enable the LoggingContext interceptor
 */
@Configuration
@Slf4j
public class LoggingContextConfig implements WebMvcConfigurer {
	@Autowired
	LoggingHandlerInterceptor interceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		log.debug("Registering LoggingHandlerInterceptor");
		registry.addInterceptor(interceptor).addPathPatterns("/**").excludePathPatterns("/simple/**");
		// see
		// https://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/mvc.html#mvc-config-interceptors
	}
}
