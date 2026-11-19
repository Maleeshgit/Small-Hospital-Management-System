package com.example.hospital_manager.security;

import com.example.hospital_manager.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

	private final String secret;
	private final long expiration;

	public JwtService(
			@Value("${jwt.secret}") String secret,
			@Value("${jwt.expiration}") long expiration) {
		this.secret = secret;
		this.expiration = expiration;
	}

	public String generateToken(String username, Role role) {
		Date issuedAt = new Date();
		Date expiresAt = new Date(issuedAt.getTime() + expiration);

		return Jwts.builder()
				.setSubject(username)
				.claim("role", role.name())
				.setIssuedAt(issuedAt)
				.setExpiration(expiresAt)
				.signWith(signingKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	public String extractUsername(String token) {
		return parseClaims(token).getSubject();
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		try {
			Claims claims = parseClaims(token);
			return claims.getSubject().equals(userDetails.getUsername())
					&& claims.getExpiration().after(new Date());
		} catch (io.jsonwebtoken.JwtException | IllegalArgumentException exception) {
			return false;
		}
	}

	public long getExpiration() {
		return expiration;
	}

	private Claims parseClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(signingKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	private Key signingKey() {
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}
}
