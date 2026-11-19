package com.example.hospital_manager.repository;

import com.example.hospital_manager.model.Patient;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {

	List<Patient> findByNameContainingIgnoreCase(String name);
}
