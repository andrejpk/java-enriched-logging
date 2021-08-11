package com.example.strucuredLogs;

import com.example.strucuredLogs.entities.TelemetryReport;
import com.example.strucuredLogs.entities.TelemetrySendResponse;

import org.slf4j.MDC;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class SimpleLogController {

	@PostMapping("/simple/telemetry")
	TelemetrySendResponse postTelemetry(@RequestBody TelemetryReport telemetryReport,
			HttpServletResponse httpResponse) {
		String correlationID = BuildCorrelationID();
		String deviceId = telemetryReport.getDeviceId();
		AddLogContext(correlationID, deviceId);

		// business logic here
		log.info("Doing an important operation importantValue={}", 123);

		// put the corr id in the response (app level)
		var response = TelemetrySendResponse.builder().correlationId(correlationID) // add correlation ID to the
				.build();

		// put the corr id in the reponse headers (infra/tooling level)
		AddCorrelationIdHeader(httpResponse, correlationID);
		ClearLogContext();
		return response;
	}

	// utility methods -- shared across projects, but included here for brevity

	static String BuildCorrelationID() {
		return "xreq_" + UUID.randomUUID().toString();
	}

	static HttpServletResponse AddCorrelationIdHeader(HttpServletResponse httpResponse, String correlationId) {
		String CORRELATION_ID_HEADER_NAME = "x-myorg-correlationid";
		httpResponse.setHeader(CORRELATION_ID_HEADER_NAME, correlationId);
		return httpResponse;
	}

	static void AddLogContext(String correlationId, String deviceId) {
		String LOG_KEY_CORRELATION_ID = "myorgCorrelationId";
		String LOG_KEY_DEVICE_ID = "deviceId";

		if (correlationId != null && !correlationId.isEmpty())
			MDC.put(LOG_KEY_CORRELATION_ID, correlationId);
		if (deviceId != null && !deviceId.isEmpty())
			MDC.put(LOG_KEY_DEVICE_ID, deviceId);
	}

	static void ClearLogContext() {
		MDC.clear();
	}

}
