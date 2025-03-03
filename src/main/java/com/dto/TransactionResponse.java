package com.dto;

import java.util.List;

import com.model.transactionInfo.ExchangeTransaction;


// === DTO Layer ===

/**
 * 交易請求的資料傳輸物件
 */
public class TransactionResponse {
	private int duplicateCount;
	private List<ExchangeTransaction> duplicates;
	private int nonDuplicateCount;
	private List<ExchangeTransaction> nonDuplicates;

	// Getter和Setter方法
	public int getDuplicateCount() {
		return duplicateCount;
	}

	public void setDuplicateCount(int duplicateCount) {
		this.duplicateCount = duplicateCount;
	}

	public List<ExchangeTransaction> getDuplicates() {
		return duplicates;
	}

	public void setDuplicates(List<ExchangeTransaction> duplicates) {
		this.duplicates = duplicates;
	}

	public int getNonDuplicateCount() {
		return nonDuplicateCount;
	}

	public void setNonDuplicateCount(int nonDuplicateCount) {
		this.nonDuplicateCount = nonDuplicateCount;
	}

	public List<ExchangeTransaction> getNonDuplicates() {
		return nonDuplicates;
	}

	public void setNonDuplicates(List<ExchangeTransaction> nonDuplicates) {
		this.nonDuplicates = nonDuplicates;
	}
}