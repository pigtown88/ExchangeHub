package com.model.transactionInfo;

//交易相關資訊，包含金額和交易詳情
public class TransactionInfo {
	private Double exchangeAmount; // 結匯金額(外幣)
	private String remittanceCode; // 匯款性質分類編號
	private String transactionDescription; // 交易商品名稱或服務類別
	private String transactionTime; // 交易更新時間
	private String transactionNumber; // 交易序號

	// getter 和 setter 方法
	public Double getExchangeAmount() {
		return exchangeAmount;
	}

	public void setExchangeAmount(Double exchangeAmount) {
		this.exchangeAmount = exchangeAmount;
	}

	public String getRemittanceCode() {
		return remittanceCode;
	}

	public void setRemittanceCode(String remittanceCode) {
		this.remittanceCode = remittanceCode;
	}

	public String getTransactionDescription() {
		return transactionDescription;
	}

	public void setTransactionDescription(String transactionDescription) {
		this.transactionDescription = transactionDescription;
	}

	public String getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}

	public String getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}
}
