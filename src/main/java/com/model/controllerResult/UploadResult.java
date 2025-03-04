package com.model.controllerResult;  // 確保這個類在正確的包中

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

import com.model.transactionInfo.ExchangeTransaction;



@XmlRootElement  // 添加這個註解以支援 XML/JSON 序列化
public class UploadResult {
    // 將所有字段設為 private
    private boolean success;
    private String message;
    private String fileName;
    private int recordCount;
    private List<ExchangeTransaction> transactions;

    // 默認構造函數
    public UploadResult() {
        this.success = true;
    }

    // 錯誤訊息構造函數
    public UploadResult(String errorMessage) {
        this.success = false;
        this.message = errorMessage;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public List<ExchangeTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<ExchangeTransaction> transactions) {
        this.transactions = transactions;
    }
}