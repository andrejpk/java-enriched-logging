package com.example.strucuredLogs.entities;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class TelemetryReport {
	@NonNull
	String deviceId;
	Double locationLat;
	Double locationLon;
}
