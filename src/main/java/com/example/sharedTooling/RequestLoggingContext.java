package com.example.sharedTooling;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Wraps LoggingContext with a Request scope. This makes the Spring DI create
 * and share one instance of LoggingContext across the request lifecyle. It is
 * created on the first Autowired dependancy and afterwards the same instance is
 * supplied, sharing that same correlation ID across the request lifecycle (even
 * in middleware like interceptors).
 */
@Component
@RequestScope
public class RequestLoggingContext extends LoggingContext {
	// private @Autowired HttpServletRequest request;

}
