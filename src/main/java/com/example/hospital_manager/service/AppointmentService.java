package com.example.hospital_manager.service;

import com.example.hospital_manager.dto.AppointmentRequest;
import com.example.hospital_manager.dto.AppointmentResponse;
import com.example.hospital_manager.dto.StatusRequest;
import com.example.hospital_manager.exception.ApiException;
import com.example.hospital_manager.model.AppUser;
import com.example.hospital_manager.model.Appointment;
import com.example.hospital_manager.model.Role;
import com.example.hospital_manager.repository.AppointmentRepository;
import com.example.hospital_manager.repository.PatientRepository;
import com.example.hospital_manager.repository.UserRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {

	private final AppointmentRepository appointmentRepository;
	private final PatientRepository patientRepository;
	private final UserRepository userRepository;

	public AppointmentService(
			AppointmentRepository appointmentRepository,
			PatientRepository patientRepository,
			UserRepository userRepository) {
		this.appointmentRepository = appointmentRepository;
		this.patientRepository = patientRepository;
		this.userRepository = userRepository;
	}

	public AppointmentResponse book(AppointmentRequest request) {
		Appointment appointment = new Appointment();
		appointment.setPatient(patientRepository.findById(request.patientId())
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Patient not found")));
		appointment.setDoctor(findDoctor(request.doctorId()));
		appointment.setAppointmentAt(request.appointmentAt());
		return toResponse(appointmentRepository.save(appointment));
	}

	public List<AppointmentResponse> findAll() {
		return appointmentRepository.findAll()
				.stream()
				.map(AppointmentService::toResponse)
				.toList();
	}

	public List<AppointmentResponse> findByDoctor(Long doctorId) {
		findDoctor(doctorId);
		return appointmentRepository.findByDoctor_Id(doctorId)
				.stream()
				.map(AppointmentService::toResponse)
				.toList();
	}

	public List<AppointmentResponse> findByDoctorUsername(String username) {
		AppUser doctor = userRepository.findByUsername(username)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Doctor not found"));
		return findByDoctor(doctor.getId());
	}

	public List<AppointmentResponse> findByPatient(Long patientId) {
		patientRepository.findById(patientId)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Patient not found"));
		return appointmentRepository.findByPatient_Id(patientId)
				.stream()
				.map(AppointmentService::toResponse)
				.toList();
	}

	public AppointmentResponse updateStatus(Long id, StatusRequest request) {
		Appointment appointment = appointmentRepository.findById(id)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Appointment not found"));
		appointment.setStatus(request.status());
		return toResponse(appointmentRepository.save(appointment));
	}

	private AppUser findDoctor(Long id) {
		AppUser doctor = userRepository.findById(id)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Doctor not found"));
		if (doctor.getRole() != Role.DOCTOR) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Selected user is not a doctor");
		}
		return doctor;
	}

	private static AppointmentResponse toResponse(Appointment appointment) {
		return new AppointmentResponse(
				appointment.getId(),
				appointment.getPatient().getId(),
				appointment.getDoctor().getId(),
				appointment.getAppointmentAt(),
				appointment.getStatus());
	}
}
