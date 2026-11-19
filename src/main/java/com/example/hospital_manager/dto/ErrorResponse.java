package com.example.hospital_manager.dto;

import java.time.Instant;

public record ErrorResponse(
		int status,
		String message,
		Instant timestamp) {
}
