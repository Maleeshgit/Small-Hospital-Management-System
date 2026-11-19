package com.example.hospital_manager.service;

import com.example.hospital_manager.dto.AuthResponse;
import com.example.hospital_manager.dto.LoginRequest;
import com.example.hospital_manager.dto.RegisterRequest;
import com.example.hospital_manager.dto.UserResponse;
import com.example.hospital_manager.exception.ApiException;
import com.example.hospital_manager.model.AppUser;
import com.example.hospital_manager.model.Role;
import com.example.hospital_manager.repository.UserRepository;
import com.example.hospital_manager.security.JwtService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public AuthService(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager,
			JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	public UserResponse register(RegisterRequest request) {
		if (userRepository.existsByUsername(request.username())) {
			throw new ApiException(HttpStatus.CONFLICT, "Username already exists");
		}

		AppUser user = new AppUser();
		user.setUsername(request.username());
		user.setPassword(passwordEncoder.encode(request.password()));
		user.setRole(request.role());

		return toResponse(userRepository.save(user));
	}

	public AuthResponse login(LoginRequest request) {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.username(), request.password()));
		} catch (AuthenticationException exception) {
			throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
		}

		AppUser user = userRepository.findByUsername(request.username())
				.orElseThrow(() -> new AuthenticationServiceException("Authenticated user was not found"));
		String token = jwtService.generateToken(user.getUsername(), user.getRole());

		return new AuthResponse(token, "Bearer", jwtService.getExpiration(), user.getRole());
	}

	public UserResponse me(String username) {
		return toResponse(findUser(username));
	}

	public List<UserResponse> listUsers() {
		return userRepository.findAll()
				.stream()
				.map(AuthService::toResponse)
				.toList();
	}

	public List<UserResponse> listDoctors() {
		return userRepository.findByRole(Role.DOCTOR)
				.stream()
				.map(AuthService::toResponse)
				.toList();
	}

	private AppUser findUser(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
	}

	private static UserResponse toResponse(AppUser user) {
		return new UserResponse(user.getId(), user.getUsername(), user.getRole());
	}
}
