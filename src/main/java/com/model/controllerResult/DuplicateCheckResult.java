package com.model.controllerResult; 

import java.util.List;

import com.model.transactionInfo.ExchangeTransaction;



public class DuplicateCheckResult {
	private List<ExchangeTransaction> duplicates;
	private List<ExchangeTransaction> nonDuplicates;
	private int duplicateCount;
	private int nonDuplicateCount;

	public List<ExchangeTransaction> getDuplicates() {
		return duplicates;
	}

	public void setDuplicates(List<ExchangeTransaction> duplicates) {
		this.duplicates = duplicates;
	}

	public List<ExchangeTransaction> getNonDuplicates() {
		return nonDuplicates;
	}

	public void setNonDuplicates(List<ExchangeTransaction> nonDuplicates) {
		this.nonDuplicates = nonDuplicates;
	}

	public int getDuplicateCount() {
		return duplicateCount;
	}

	public void setDuplicateCount(int duplicateCount) {
		this.duplicateCount = duplicateCount;
	}

	public int getNonDuplicateCount() {
		return nonDuplicateCount;
	}

	public void setNonDuplicateCount(int nonDuplicateCount) {
		this.nonDuplicateCount = nonDuplicateCount;
	}

	
}