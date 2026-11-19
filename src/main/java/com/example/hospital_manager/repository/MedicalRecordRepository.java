package com.example.hospital_manager.repository;

import com.example.hospital_manager.model.MedicalRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

	List<MedicalRecord> findByPatient_Id(Long patientId);

	boolean existsByPatient_Id(Long patientId);
}
