package com.model.controllerResult;
/**
 * 存放錯誤響應的資料傳輸物件。
 */
public class ErrorResponse {
	private String message;

	public ErrorResponse(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
}