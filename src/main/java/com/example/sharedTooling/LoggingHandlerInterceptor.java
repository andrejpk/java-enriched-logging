package com.example.sharedTooling;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

@Component
@RequestScope
@Slf4j
public class LoggingHandlerInterceptor implements HandlerInterceptor {
	public static final String DEFAULT_DIAGNOSTIC_ID_HEADER = "diagnostic_id";

	@Autowired
	public RequestLoggingContext requestLoggingContext;

	public LoggingHandlerInterceptor() {
		log.debug("LoggingHandlerInterceptor created");
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// your code
		AddDiagnosticIdHeader(response);
		return true;
	}

	/**
	 * Post-request hook -- clears diag context (even on failure)
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		// clear the logging context to clear the data from this thread even if the
		// controller method crashes
		requestLoggingContext.clear();
	}

	/***
	 * Add an HTTP header for the diagonstic ID
	 * 
	 * @param response
	 */
	private void AddDiagnosticIdHeader(HttpServletResponse response) {
		String diagnosticId = requestLoggingContext.getDiagnosticId();
		log.debug("Adding diagnostic header named {} value {}", DEFAULT_DIAGNOSTIC_ID_HEADER, diagnosticId);
		// Put the app correlation ID the HTTP response header
		response.setHeader(DEFAULT_DIAGNOSTIC_ID_HEADER, diagnosticId);
	}
}
