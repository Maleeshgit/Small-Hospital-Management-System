package com.example.hospital_manager.repository;

import com.example.hospital_manager.model.Appointment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

	List<Appointment> findByDoctor_Id(Long doctorId);

	List<Appointment> findByPatient_Id(Long patientId);

	boolean existsByPatient_Id(Long patientId);
}
