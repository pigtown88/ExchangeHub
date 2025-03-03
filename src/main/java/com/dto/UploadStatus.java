package com.dto;

//UploadStatus.java
public class UploadStatus {
	private boolean hasUploaded;
	private String lastUploadTime;

	// Getters and Setters
	public boolean isHasUploaded() {
		return hasUploaded;
	}
    //true代表說已經上傳過
	//false代表說沒有上傳過
	public void setHasUploaded(boolean hasUploaded) {
		this.hasUploaded = hasUploaded;
	}

	public String getLastUploadTime() {
		return lastUploadTime;
	}

	public void setLastUploadTime(String lastUploadTime) {
		this.lastUploadTime = lastUploadTime;
	}
}
