package com.example.hospital_manager.service;

import com.example.hospital_manager.dto.MedicalRecordRequest;
import com.example.hospital_manager.dto.MedicalRecordResponse;
import com.example.hospital_manager.exception.ApiException;
import com.example.hospital_manager.model.AppUser;
import com.example.hospital_manager.model.MedicalRecord;
import com.example.hospital_manager.model.Role;
import com.example.hospital_manager.repository.MedicalRecordRepository;
import com.example.hospital_manager.repository.PatientRepository;
import com.example.hospital_manager.repository.UserRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class MedicalRecordService {

	private final MedicalRecordRepository medicalRecordRepository;
	private final PatientRepository patientRepository;
	private final UserRepository userRepository;

	public MedicalRecordService(
			MedicalRecordRepository medicalRecordRepository,
			PatientRepository patientRepository,
			UserRepository userRepository) {
		this.medicalRecordRepository = medicalRecordRepository;
		this.patientRepository = patientRepository;
		this.userRepository = userRepository;
	}

	public MedicalRecordResponse create(MedicalRecordRequest request, String doctorUsername) {
		AppUser doctor = userRepository.findByUsername(doctorUsername)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Doctor not found"));
		if (doctor.getRole() != Role.DOCTOR) {
			throw new ApiException(HttpStatus.FORBIDDEN, "Only doctors can create medical records");
		}

		MedicalRecord record = new MedicalRecord();
		record.setPatient(patientRepository.findById(request.patientId())
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Patient not found")));
		record.setDoctor(doctor);
		record.setDiagnosis(request.diagnosis());
		record.setPrescription(request.prescription());
		return toResponse(medicalRecordRepository.save(record));
	}

	public List<MedicalRecordResponse> findByPatient(Long patientId) {
		patientRepository.findById(patientId)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Patient not found"));
		return medicalRecordRepository.findByPatient_Id(patientId)
				.stream()
				.map(MedicalRecordService::toResponse)
				.toList();
	}

	private static MedicalRecordResponse toResponse(MedicalRecord record) {
		return new MedicalRecordResponse(
				record.getId(),
				record.getPatient().getId(),
				record.getDoctor().getId(),
				record.getDiagnosis(),
				record.getPrescription(),
				record.getCreatedAt());
	}
}
