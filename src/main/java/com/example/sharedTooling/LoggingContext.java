package com.example.sharedTooling;

import java.util.UUID;

import org.slf4j.MDC;

import lombok.extern.slf4j.Slf4j;

/**
 * Handles creading and clearing logging conext via SLF4J's MDC. Also creates
 * and keeps a diagnostic ID that can be used to connect requests and data back
 * to logs.
 */
@Slf4j
public class LoggingContext implements AutoCloseable {
	public static final String DIAGNOSTIC_ID_PREFIX = "diag_";
	public static final String DIAGNOSTIC_ID_KEY = "diag_id";

	private String diagnosticId;

	/** Create a correlation ID for this LoggingContext and store it in MDC */
	public LoggingContext() {
		diagnosticId = BuildDiagnosticID();
		log.debug("LoggingContext created diagonsticId {}", diagnosticId);
		MDC.put(DIAGNOSTIC_ID_KEY, diagnosticId);
	}

	/**
	 * Get the correlation ID for this LoggigContext. This is useful for storing in
	 * data payloads and returning to clients
	 */
	public String getDiagnosticId() {
		return this.diagnosticId;
	}

	/**
	 * Clears the logging context manually; this can also be done via the
	 * Autocloseable interface/convention
	 */
	public void clear() {
		log.debug("Clearing diagonstic context");
		MDC.clear();
	}

	/**
	 * Adds additonal context to the LoggingContext. This is useful for connecting a
	 * business entity or another useful lookup value to the logs.
	 * 
	 * @param key
	 * @param value
	 */
	public void setLogContext(String key, String value) {
		MDC.put(key, value);
	}

	/** Defines the process for creating a diagnostic ID */
	public static String BuildDiagnosticID() {
		return DIAGNOSTIC_ID_PREFIX + UUID.randomUUID().toString();
	}

	// Autocloseable impelmentation

	/**
	 * Implement the Autocloseable interface which cleans up MDC. A consumer can use
	 * this in a block of code by wrapping the LoggingContext init in a try block.
	 * See
	 * https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
	 * 
	 * @throws Exception
	 */
	@Override
	public void close() throws Exception {
		// clear the logging context as the LoggingContext is disposed
		clear();
	}
}
