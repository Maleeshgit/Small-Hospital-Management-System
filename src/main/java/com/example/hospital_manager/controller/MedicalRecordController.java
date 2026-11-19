package com.example.hospital_manager.controller;

import com.example.hospital_manager.dto.MedicalRecordRequest;
import com.example.hospital_manager.dto.MedicalRecordResponse;
import com.example.hospital_manager.service.MedicalRecordService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/records")
public class MedicalRecordController {

	private final MedicalRecordService medicalRecordService;

	public MedicalRecordController(MedicalRecordService medicalRecordService) {
		this.medicalRecordService = medicalRecordService;
	}

	@PostMapping
	@PreAuthorize("hasRole('DOCTOR')")
	public MedicalRecordResponse create(
			@Valid @RequestBody MedicalRecordRequest request,
			Authentication authentication) {
		return medicalRecordService.create(request, authentication.getName());
	}

	@GetMapping("/patient/{patientId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
	public List<MedicalRecordResponse> findByPatient(@PathVariable Long patientId) {
		return medicalRecordService.findByPatient(patientId);
	}
}
