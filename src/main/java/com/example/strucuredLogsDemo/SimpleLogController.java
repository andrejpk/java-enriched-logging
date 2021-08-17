package com.example.strucuredLogsDemo;

import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import com.example.strucuredLogsDemo.entities.TelemetryReport;
import com.example.strucuredLogsDemo.entities.TelemetrySendResponse;

import org.slf4j.MDC;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * Shows a manual implementation of the diagnostic pattern without sharedTooling
 */
@RestController
@Slf4j
public class SimpleLogController {

	@PostMapping("/simple/telemetry")
	TelemetrySendResponse postTelemetry(@Validated @RequestBody TelemetryReport telemetryReport,
			HttpServletResponse httpResponse) {
		String diagnosticID = BuildDiagnosticID();
		String deviceId = telemetryReport.getDeviceId();
		AddLogContext(diagnosticID, deviceId);

		// business logic here
		log.info("Simple: Doing an important operation importantValue={}", 123);

		// put the corr id in the response (app level)
		var response = TelemetrySendResponse.builder().diagnosticId(diagnosticID) // add diagnostic ID to the
				.build();

		// put the corr id in the reponse headers (infra/tooling level)
		AddDiagnosticIdHeader(httpResponse, diagnosticID);
		ClearLogContext();
		return response;
	}

	// utility methods -- shared across projects, but included here for brevity

	static String BuildDiagnosticID() {
		return "xreq_" + UUID.randomUUID().toString();
	}

	static HttpServletResponse AddDiagnosticIdHeader(HttpServletResponse httpResponse, String diagnosticId) {
		String DIAGNOSTIC_ID_HEADER_NAME = "x-myorg-diagnosticid";
		httpResponse.setHeader(DIAGNOSTIC_ID_HEADER_NAME, diagnosticId);
		return httpResponse;
	}

	static void AddLogContext(String diagnosticId, String deviceId) {
		String LOG_KEY_DIAGNOSTIC_ID = "myorgDiagnosticId";
		String LOG_KEY_DEVICE_ID = "deviceId";

		if (diagnosticId != null && !diagnosticId.isEmpty())
			MDC.put(LOG_KEY_DIAGNOSTIC_ID, diagnosticId);
		if (deviceId != null && !deviceId.isEmpty())
			MDC.put(LOG_KEY_DEVICE_ID, deviceId);
	}

	static void ClearLogContext() {
		MDC.clear();
	}

}
