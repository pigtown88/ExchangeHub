package com.service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.dto.TransactionResponse;
import com.model.transactionInfo.ExchangeTransaction;
import com.model.transactionInfo.PersonalInfo;
import com.model.transactionInfo.TransactionInfo;


public class ExportDuplicateCsvService {
    
    private static final String CSV_HEADER = 
        "交易序號,姓名,身分證字號,生日,國籍,居留證核發日期,居留證有效期限," +
        "電話,幣別,結匯金額,匯款性質分類編號,交易商品描述,交易時間\n";
        
    /**
     * 生成包含重複交易資料的 CSV 內容
     */
    public byte[] generateDuplicateTransactionsCsv(TransactionResponse data) 
            throws IOException {
        StringBuilder csv = new StringBuilder();
        csv.append(CSV_HEADER);

        for (ExchangeTransaction transaction : data.getDuplicates()) {
            csv.append(convertTransactionToCsvRow(transaction));
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 產生 CSV 檔案名稱
     */
    public String generateCsvFileName() {
        return String.format("duplicate_transactions_%s.csv",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
    }

    /**
     * 將單筆交易轉換為 CSV 行格式
     */
    private String convertTransactionToCsvRow(ExchangeTransaction transaction) {
        PersonalInfo pi = transaction.getPersonalInfo();
        TransactionInfo ti = transaction.getTransactionInfo();

        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%.2f,%s,%s,%s\n",
            escapeField(ti.getTransactionNumber()),
            escapeField(pi.getName()),
            escapeField(pi.getIdNumber()),
            escapeField(pi.getBirthDate()),
            escapeField(pi.getNationality()),
            escapeField(pi.getResidencePermitIssueDate()),
            escapeField(pi.getResidencePermitExpiryDate()),
            escapeField(pi.getPhoneNumber()),
            escapeField(pi.getCurrency()),
            ti.getExchangeAmount(),
            escapeField(ti.getRemittanceCode()),
            escapeField(ti.getTransactionDescription()),
            escapeField(ti.getTransactionTime())
        );
    }

    /**
     * 處理 CSV 欄位中的特殊字元
     */
    private String escapeField(String field) {
        if (field == null) {
            return "";
        }
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
}