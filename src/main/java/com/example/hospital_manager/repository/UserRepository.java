package com.example.hospital_manager.repository;

import com.example.hospital_manager.model.AppUser;
import com.example.hospital_manager.model.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {

	Optional<AppUser> findByUsername(String username);

	List<AppUser> findByRole(Role role);

	boolean existsByUsername(String username);
}
