package com.example.strucuredLogsDemo;

import com.example.sharedTooling.RequestLoggingContext;
import com.example.strucuredLogsDemo.entities.TelemetryReport;
import com.example.strucuredLogsDemo.entities.TelemetrySendResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * Demonstrates using the sharedTooling to automatically generate a
 * diagnosticId, add controller-based context to the logs, and return a
 * diagnostic ID in the HTTP header. (Most of this is done automatically through
 * the configuration in StructuredLogsConfig)
 */
@RestController
@Slf4j
public class AutoLoggingController {
	static final String LOG_KEY_DEVICE_ID = "deviceId";

	@Autowired
	RequestLoggingContext loggingContext;

	@PostMapping("/auto/telemetry")
	TelemetrySendResponse postTelemetry(@Validated @RequestBody TelemetryReport telemetryReport) {
		// add deviceId to the logging context
		String deviceId = telemetryReport.getDeviceId();
		loggingContext.setLogContext(LOG_KEY_DEVICE_ID, deviceId);

		// business logic here
		log.info("Auto: Doing an important operation importantValue={}", 123);

		// put the diag id in the response (app level)
		var response = TelemetrySendResponse.builder().diagnosticId(loggingContext.getDiagnosticId()).build();

		return response;
	}
}
