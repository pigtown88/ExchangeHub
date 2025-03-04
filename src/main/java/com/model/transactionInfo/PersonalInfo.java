package com.model.transactionInfo;

//個人基本資訊，包含身分相關資料
public class PersonalInfo {
	private String name; // 姓名
	private String idNumber; // ID/居留證
	private String birthDate; // 生日
	private String nationality; // 國籍
	private String residencePermitIssueDate; // 居留證核發日期
	private String residencePermitExpiryDate; // 居留證有效期限
	private String phoneNumber; // 電話
	private String currency; // 幣別

	// getter 和 setter 方法
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getResidencePermitIssueDate() {
		return residencePermitIssueDate;
	}

	public void setResidencePermitIssueDate(String residencePermitIssueDate) {
		this.residencePermitIssueDate = residencePermitIssueDate;
	}

	public String getResidencePermitExpiryDate() {
		return residencePermitExpiryDate;
	}

	public void setResidencePermitExpiryDate(String residencePermitExpiryDate) {
		this.residencePermitExpiryDate = residencePermitExpiryDate;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
}
