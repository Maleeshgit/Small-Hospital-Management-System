package com.example.hospital_manager.dto;

import java.time.LocalDateTime;

public record MedicalRecordResponse(
		Long id,
		Long patientId,
		Long doctorId,
		String diagnosis,
		String prescription,
		LocalDateTime createdAt) {
}
