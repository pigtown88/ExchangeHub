package com.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.dao.ExchangeTransactionDao;
import com.model.*;
import com.model.threeInOneEncryption.ProcessResult;
import com.model.transactionInfo.ExchangeTransaction;
import com.model.transactionInfo.PersonalInfo;
import com.model.transactionInfo.TransactionInfo;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class FinalProcessUploadService {
    private static final Logger log = LogManager.getLogger(PersistenceService.class);
    private final ExchangeTransactionDao exchangeDao;

    public FinalProcessUploadService(ExchangeTransactionDao exchangeDao) {
        this.exchangeDao = exchangeDao;
    }

    public ProcessResult checkTodayStatusAndInsert(List<ExchangeTransaction> nonDuplicates, 
            List<ExchangeTransaction> duplicates) throws SQLException {
        try {
            LocalDateTime startTime = LocalDateTime.now();
            int savedCount = 0;

            // 遍歷並處理每筆非重複交易
            for (ExchangeTransaction transaction : nonDuplicates) {
                // 對交易資料進行 URL 解碼
                ExchangeTransaction decodedTransaction = decodeTransaction(transaction);
                
                // 記錄解碼後的資料狀態
                log.debug("解碼後的交易資料: 姓名={}, 國籍={}, 交易描述={}", 
                    decodedTransaction.getPersonalInfo().getName(),
                    decodedTransaction.getPersonalInfo().getNationality(),
                    decodedTransaction.getTransactionInfo().getTransactionDescription());

                // 儲存解碼後的交易資料
                if (exchangeDao.insert(decodedTransaction)) {
                    savedCount++;
                }
            }

            // 計算處理時間和建立結果
            LocalDateTime endTime = LocalDateTime.now();
            String processTime = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            ProcessResult result = new ProcessResult();
            result.setSuccess(true);
            result.setProcessedCount(savedCount);
            result.setDuplicateCount(duplicates != null ? duplicates.size() : 0);
            result.setProcessTime(processTime);
            result.setMessage("資料處理完成");

            log.info("成功處理上傳資料: 處理{}筆, 重複{}筆", savedCount, result.getDuplicateCount());
            return result;

        } catch (SQLException e) {
            log.error("處理交易資料時發生錯誤", e);
            throw e;
        }
    }

    /**
     * 解碼交易資料中的 URL 編碼字段
     */
    private ExchangeTransaction decodeTransaction(ExchangeTransaction transaction) {
        try {
            // 創建新的交易物件以避免修改原始資料
            ExchangeTransaction decoded = new ExchangeTransaction();
            
            // 解碼個人資訊
            PersonalInfo decodedPersonalInfo = new PersonalInfo();
            PersonalInfo originalInfo = transaction.getPersonalInfo();
            
            // 解碼姓名和國籍
            decodedPersonalInfo.setName(decodeIfNeeded(originalInfo.getName()));
            decodedPersonalInfo.setNationality(decodeIfNeeded(originalInfo.getNationality()));
            
            // 複製其他不需解碼的欄位
            decodedPersonalInfo.setBirthDate(originalInfo.getBirthDate());
            decodedPersonalInfo.setIdNumber(originalInfo.getIdNumber());
            decodedPersonalInfo.setPhoneNumber(originalInfo.getPhoneNumber());
            decodedPersonalInfo.setCurrency(originalInfo.getCurrency());
            decodedPersonalInfo.setResidencePermitExpiryDate(originalInfo.getResidencePermitExpiryDate());
            decodedPersonalInfo.setResidencePermitIssueDate(originalInfo.getResidencePermitIssueDate());
            
            // 解碼交易資訊
            TransactionInfo decodedTransactionInfo = new TransactionInfo();
            TransactionInfo originalTransInfo = transaction.getTransactionInfo();
            
            // 解碼交易描述
            decodedTransactionInfo.setTransactionDescription(
                decodeIfNeeded(originalTransInfo.getTransactionDescription()));
            
            // 複製其他不需解碼的欄位
            decodedTransactionInfo.setExchangeAmount(originalTransInfo.getExchangeAmount());
            decodedTransactionInfo.setRemittanceCode(originalTransInfo.getRemittanceCode());
            decodedTransactionInfo.setTransactionTime(originalTransInfo.getTransactionTime());
            decodedTransactionInfo.setTransactionNumber(originalTransInfo.getTransactionNumber());
            
            // 設置解碼後的資訊
            decoded.setPersonalInfo(decodedPersonalInfo);
            decoded.setTransactionInfo(decodedTransactionInfo);
            
            return decoded;
            
        } catch (Exception e) {
            log.error("解碼交易資料時發生錯誤", e);
            throw new RuntimeException("解碼失敗: " + e.getMessage());
        }
    }

    /**
     * 如果字串是 URL 編碼的，進行解碼；否則返回原始字串
     */
    private String decodeIfNeeded(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        try {
            // 檢查是否為 URL 編碼的字串
            if (text.contains("%")) {
                return URLDecoder.decode(text, StandardCharsets.UTF_8.name());
            }
            return text;
        } catch (Exception e) {
            log.error("URL解碼失敗: {}", text, e);
            return text;
        }
    }
}