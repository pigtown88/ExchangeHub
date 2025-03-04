package com.model.member;

public class AuthErrorResponse {
	private String errorCode;
	private String errorType;
	private String errorMessage;

	public AuthErrorResponse(String errorCode, String errorType, String errorMessage) {
		this.errorCode = errorCode;
		this.errorType = errorType;
		this.errorMessage = errorMessage;
	}

	// Getters
	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorType() {
		return errorType;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
