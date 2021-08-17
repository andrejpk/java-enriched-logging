# Structured logging and logging patterns

This repo shares some examples, patterns and reusable tools for logging in Java services, primary focusing on adding structured data to logs.

The goal is to easily connect logging data, request responses and data payloads using logging context. This doesn't take the place of telemetry correlation provided by W3C's Trace Context. It's an application-level ID that's controlled and available at the app level, so a good way to connect app/user level information with the correlation provided by services like Azure Application Insights.

The approach is to use SLF4J's MDC to capture and store a diagnostic ID that can be returned to clients and embedded into data. This gives operators a key to trace back to logs. It also defines a standard way to link other context (business entity IDs) into the logs, making it easier to find and filter logs.

Example controllers:

[SimpleLogController](src/main/java/com/example/strucuredLogsDemo/SimpleLogController.java) implements this pattern 'manually' in the controller iself.

[AutoLoggingController](src/main/java/com/example/strucuredLogsDemo/AutoLoggingController.java) implements this pattern using the tooling provided here.

Configuration:

The `LoggingContextConfig` class configures and enables the automatic diagnostic interceptor that adds the `diagonstic_id` HTTP header and automatically clears the MDC even after a failed request. To enable it, it must be scanned by Spring adding a `ComponentScan` annotation:

```java
@Configuration
@ComponentScan(basePackages = "com.example.sharedTooling")
public class StructuredLogsConfig implements WebMvcConfigurer
...
```

[StructuredLogsConfig] demonstrantes using Spring's `CommonsRequestLoggingFilter` to log each request. This may not be a good idea in production, especially with payloads, but shows how to use this tool where it makes sense. (This is indpedant of the other tooling here but follows the theme.) Consider using this or extending it if you'd like to have reqest operations appear in logs (instead of adding log calls at the start of controllers). This is probably not needed in App Insights since it captures requests through its agent's bindings with the JVM and SDKs.

## Using

To enable the tooling on a project, extract the sharedTooling components to a shared library or include them direclty in the project. Follow the pattern in `LoggingContextConfig` to bind to all rest controllers, using filters to exclude some if desired. This can also be configured in XML.

At this point, log messages with controllers will carry in their MDC a `diagnosticId` that's unique for each request and the responses will include a `diagnostic_id` header with that same value.

A controller can get access to this `diagnosticId` and set other key/values by `@Autowire`ing in RequestLoggingContext:

```java
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
```

Calling this controller returns the following headers:

```text
diagnostic_id: diag_8a369f55-ace8-4e98-b620-d07c9a65ba10
Content-Type: application/json
Transfer-Encoding: chunked
Date: ...
```

and payload:

```json
{
  "diagnosticId": "diag_8a369f55-ace8-4e98-b620-d07c9a65ba10"
}
```

and logs:

```text
Before request [POST /auto/telemetry]   MDC:
LoggingHandlerInterceptor created   MDC:
LoggingContext created diagonsticId diag_df2959d0-70db-425e-92cf-76e98938700b   MDC:
Adding diagnostic header named diagnostic_id value diag_df2959d0-70db-425e-92cf-76e98938700b   MDC:diag_id=diag_df2959d0-70db-425e-92cf-76e98938700b
Auto: Doing an important operation importantValue=123   MDC:diag_id=diag_df2959d0-70db-425e-92cf-76e98938700b, deviceId=device123
Clearing diagonstic context   MDC:diag_id=diag_df2959d0-70db-425e-92cf-76e98938700b, deviceId=device123
Clearing diagonstic context   MDC:
REQUEST DATA : POST /auto/telemetry, payload={
        "deviceId": "device123",
        "locationLat": 84,
        "locationLon": 120
}]   MDC:
```

## Tooling

[LoggingContext](src/main/java/com/example/sharedTooling/LoggingContext.java) manages the MDC, creates/manages the `diagnsticId`, supports Java's resource management mechanism to clean up even after exceptions. Use this in custom components handling work but not running in Spring controllers.

[RequestLoggingContext](bin/main/com/example/sharedTooling/RequestLoggingContext.class) maps `LoggingContext` into the lifetime of a request in a Spring REST controller. `@Autowire` this into a controller to get access to the `diagnosticId` or set custom key/values on the log context.

[LoggingHandlerInterceptor](bin/main/com/example/sharedTooling/LoggingHandlerInterceptor.class) automatically wires up `RequestLoggingInterceptor` to every request, adding the `diagnostic_id` header and cleaning up at the end of a request.

[LoggingContextConfig](bin/main/com/example/sharedTooling/LoggingContextConfig.class) wires `LoggingHandlerInterceptor` into each Spring REST controller. Shows how to include/exclude some endpoints.
