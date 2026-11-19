package com.example.hospital_manager.dto;

import com.example.hospital_manager.model.AppointmentStatus;
import java.time.LocalDateTime;

public record AppointmentResponse(
		Long id,
		Long patientId,
		Long doctorId,
		LocalDateTime appointmentAt,
		AppointmentStatus status) {
}
