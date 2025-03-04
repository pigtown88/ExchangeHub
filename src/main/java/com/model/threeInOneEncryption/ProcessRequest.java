package com.model.threeInOneEncryption;

import java.util.List;

import com.model.transactionInfo.ExchangeTransaction;



//ProcessRequest.java
public class ProcessRequest {
	private List<ExchangeTransaction> nonDuplicates;
	private List<ExchangeTransaction> duplicates;

	public List<ExchangeTransaction> getNonDuplicates() {
		return nonDuplicates;
	}

	public void setNonDuplicates(List<ExchangeTransaction> nonDuplicates) {
		this.nonDuplicates = nonDuplicates;
	}

	public List<ExchangeTransaction> getDuplicates() {
		return duplicates;
	}

	public void setDuplicates(List<ExchangeTransaction> duplicates) {
		this.duplicates = duplicates;
	}

}
