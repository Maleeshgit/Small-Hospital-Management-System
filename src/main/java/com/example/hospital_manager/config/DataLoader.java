package com.example.hospital_manager.config;

import com.example.hospital_manager.model.AppUser;
import com.example.hospital_manager.model.Patient;
import com.example.hospital_manager.model.Role;
import com.example.hospital_manager.repository.PatientRepository;
import com.example.hospital_manager.repository.UserRepository;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

	private final UserRepository userRepository;
	private final PatientRepository patientRepository;
	private final PasswordEncoder passwordEncoder;

	public DataLoader(
			UserRepository userRepository,
			PatientRepository patientRepository,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.patientRepository = patientRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {
		createUserIfMissing("admin", "admin123", Role.ADMIN);
		createUserIfMissing("doctor", "doctor123", Role.DOCTOR);
		createUserIfMissing("patient", "patient123", Role.PATIENT);

		if (patientRepository.count() == 0) {
			patientRepository.save(new Patient(
					null,
					"John Doe",
					LocalDate.of(1985, 5, 20),
					"john.doe@example.com",
					"0712345678"));
			patientRepository.save(new Patient(
					null,
					"Jane Smith",
					LocalDate.of(1992, 9, 14),
					"jane.smith@example.com",
					"0723456789"));
		}
	}

	private void createUserIfMissing(String username, String password, Role role) {
		if (userRepository.existsByUsername(username)) {
			return;
		}

		AppUser user = new AppUser();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));
		user.setRole(role);
		userRepository.save(user);
	}
}
