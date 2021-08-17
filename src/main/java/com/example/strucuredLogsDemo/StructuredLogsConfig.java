package com.example.strucuredLogsDemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Example of using Spring's build-in request logger. This adds a log message
 * for being and end request
 */
@Configuration
@ComponentScan(basePackages = "com.example.sharedTooling")
public class StructuredLogsConfig implements WebMvcConfigurer {

	/**
	 * Adds a request logger, adding a log message before and after each request
	 * runs on DEBUG log level, by default
	 * 
	 * @return
	 */
	@Bean
	public CommonsRequestLoggingFilter logFilter() {
		CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
		filter.setIncludeQueryString(true);
		filter.setIncludePayload(true); // will make the logs big, but this logger enabled for DEBUG level only
		filter.setMaxPayloadLength(10000);
		filter.setIncludeHeaders(false);
		filter.setAfterMessagePrefix("REQUEST DATA : ");
		return filter;
	}
}
