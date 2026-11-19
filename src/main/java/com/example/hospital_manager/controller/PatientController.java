package com.example.hospital_manager.controller;

import com.example.hospital_manager.model.Patient;
import com.example.hospital_manager.service.PatientService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/patients")
@PreAuthorize("hasRole('ADMIN')")
public class PatientController {

	private final PatientService patientService;

	public PatientController(PatientService patientService) {
		this.patientService = patientService;
	}

	@GetMapping
	public List<Patient> findAll(@RequestParam(required = false) String name) {
		return name == null || name.isBlank()
				? patientService.findAll()
				: patientService.searchByName(name);
	}

	@GetMapping("/{id}")
	public Patient findById(@PathVariable Long id) {
		return patientService.findById(id);
	}

	@PostMapping
	public ResponseEntity<Patient> create(@Valid @RequestBody Patient patient) {
		return ResponseEntity.status(HttpStatus.CREATED).body(patientService.create(patient));
	}

	@PutMapping("/{id}")
	public Patient update(@PathVariable Long id, @Valid @RequestBody Patient patient) {
		return patientService.update(id, patient);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		patientService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
