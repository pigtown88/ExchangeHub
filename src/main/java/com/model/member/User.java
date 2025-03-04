package com.model.member;

import java.time.LocalDateTime;
import java.util.List;

public class User {
	private Long id;
	private String username;
	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	private String password;
	private String keyId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private List<UserToken> tokens;

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<UserToken> getTokens() {
		return tokens;
	}

	public void setTokens(List<UserToken> tokens) {
		this.tokens = tokens;
	}
}
