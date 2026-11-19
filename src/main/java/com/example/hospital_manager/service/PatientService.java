package com.example.hospital_manager.service;

import com.example.hospital_manager.exception.ApiException;
import com.example.hospital_manager.model.Patient;
import com.example.hospital_manager.repository.AppointmentRepository;
import com.example.hospital_manager.repository.MedicalRecordRepository;
import com.example.hospital_manager.repository.PatientRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

	private final PatientRepository patientRepository;
	private final AppointmentRepository appointmentRepository;
	private final MedicalRecordRepository medicalRecordRepository;

	public PatientService(
			PatientRepository patientRepository,
			AppointmentRepository appointmentRepository,
			MedicalRecordRepository medicalRecordRepository) {
		this.patientRepository = patientRepository;
		this.appointmentRepository = appointmentRepository;
		this.medicalRecordRepository = medicalRecordRepository;
	}

	public Patient create(Patient patient) {
		return patientRepository.save(patient);
	}

	public List<Patient> findAll() {
		return patientRepository.findAll();
	}

	public List<Patient> searchByName(String name) {
		return patientRepository.findByNameContainingIgnoreCase(name);
	}

	public Patient findById(Long id) {
		return patientRepository.findById(id)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Patient not found"));
	}

	public Patient update(Long id, Patient patient) {
		Patient existing = findById(id);
		existing.setName(patient.getName());
		existing.setDateOfBirth(patient.getDateOfBirth());
		existing.setEmail(patient.getEmail());
		existing.setPhone(patient.getPhone());
		return patientRepository.save(existing);
	}

	public void delete(Long id) {
		Patient patient = findById(id);
		if (appointmentRepository.existsByPatient_Id(id)
				|| medicalRecordRepository.existsByPatient_Id(id)) {
			throw new ApiException(
					HttpStatus.CONFLICT,
					"Cannot delete a patient with appointments or medical records");
		}

		patientRepository.delete(patient);
	}
}
