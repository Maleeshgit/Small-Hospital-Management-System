package com.example.hospital_manager.controller;

import com.example.hospital_manager.dto.AppointmentRequest;
import com.example.hospital_manager.dto.AppointmentResponse;
import com.example.hospital_manager.dto.StatusRequest;
import com.example.hospital_manager.service.AppointmentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AppointmentController {

	private final AppointmentService appointmentService;

	public AppointmentController(AppointmentService appointmentService) {
		this.appointmentService = appointmentService;
	}

	@PostMapping("/patients/appointments")
	@PreAuthorize("hasRole('PATIENT')")
	public AppointmentResponse book(@Valid @RequestBody AppointmentRequest request) {
		return appointmentService.book(request);
	}

	@GetMapping("/doctors/appointments")
	@PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
	public List<AppointmentResponse> doctorAppointments(
			@RequestParam(required = false) Long doctorId,
			Authentication authentication) {
		if (isAdmin(authentication)) {
			return doctorId == null
					? appointmentService.findAll()
					: appointmentService.findByDoctor(doctorId);
		}
		return appointmentService.findByDoctorUsername(authentication.getName());
	}

	@GetMapping("/patients/{patientId}/appointments")
	@PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
	public List<AppointmentResponse> patientAppointments(@PathVariable Long patientId) {
		return appointmentService.findByPatient(patientId);
	}

	@PatchMapping("/appointments/{id}/status")
	@PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
	public AppointmentResponse updateStatus(
			@PathVariable Long id,
			@Valid @RequestBody StatusRequest request) {
		return appointmentService.updateStatus(id, request);
	}

	private boolean isAdmin(Authentication authentication) {
		return authentication.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.anyMatch("ROLE_ADMIN"::equals);
	}
}
