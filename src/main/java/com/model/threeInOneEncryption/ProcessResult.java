package com.model.threeInOneEncryption;

/**
 * 處理結果類
 */
public class ProcessResult {
	private boolean success; //成功與否
	private String message; //其他訊息
	private int processedCount; //總交易比數
	private int duplicateCount; //重複交易筆數
	private int uniqueCount;  //不重複交易筆數
	private String ProcessTime; //其他訊息


	// Getters and Setters
	
	
	
	public boolean isSuccess() {
		return success;
	}

	public String getProcessTime() {
		return ProcessTime;
	}

	public void setProcessTime(String processTime) {
		ProcessTime = processTime;
	}

	public int getUniqueCount() {
		return uniqueCount;
	}

	public void setUniqueCount(int uniqueCount) {
		this.uniqueCount = uniqueCount;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getProcessedCount() {
		return processedCount;
	}

	public void setProcessedCount(int processedCount) {
		this.processedCount = processedCount;
	}

	public int getDuplicateCount() {
		return duplicateCount;
	}

	public void setDuplicateCount(int duplicateCount) {
        this.duplicateCount = duplicateCount;
    }
	
	//計算不重複的交易筆數
	private void calculateUniqueCount() {
		this.uniqueCount	=  this.processedCount - this.duplicateCount;
		
	}
}