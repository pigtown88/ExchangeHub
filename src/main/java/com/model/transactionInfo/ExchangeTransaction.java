package com.model.transactionInfo;

public class ExchangeTransaction {
	// 個人基本資料
	private PersonalInfo personalInfo;

	// 交易資訊
	private TransactionInfo transactionInfo;

	// getter 和 setter
	public PersonalInfo getPersonalInfo() {
		return personalInfo;
	}

	public void setPersonalInfo(PersonalInfo personalInfo) {
		this.personalInfo = personalInfo;
	}

	public TransactionInfo getTransactionInfo() {
		return transactionInfo;
	}

	public void setTransactionInfo(TransactionInfo transactionInfo) {
		this.transactionInfo = transactionInfo;
	}
}



