package com.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dao.ExchangeTransactionDao;
import com.dto.UploadStatus;
import com.model.transactionInfo.ExchangeTransaction;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class PersistenceService {
    private static final Logger log = LogManager.getLogger(PersistenceService.class);
    
    private final ExchangeTransactionDao exchangeDao;

   
    public PersistenceService(ExchangeTransactionDao exchangeDao) {
        this.exchangeDao = exchangeDao;
    }

    /*
     * 檢查當日上傳狀態
     * @return UploadStatus 上傳狀態物件
     */
    public UploadStatus checkTodayUploadStatus() throws SQLException {
        try {
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            // 從資料庫獲取當日最後上傳時間
            String lastUploadTime = exchangeDao.getLastUploadTimeForDate(today);
            
            UploadStatus status = new UploadStatus();
            status.setHasUploaded(lastUploadTime != null);
            status.setLastUploadTime(lastUploadTime);
            
            log.info("檢查當日上傳狀態: hasUploaded={}, lastUploadTime={}", 
                    status.isHasUploaded(), status.getLastUploadTime());
            
            return status;
            
        } catch (SQLException e) {
            log.error("檢查當日上傳狀態時發生錯誤", e);
            throw e;
        }
    }
    
    /**
     * 儲存交易資料到資料庫
     */
    public void saveTransactions(List<ExchangeTransaction> transactions) throws SQLException {
        int successCount = 0;
        for (ExchangeTransaction transaction : transactions) {
            try {
                if (exchangeDao.insert(transaction)) {
                    successCount++;
                }
            } catch (SQLException e) {
                log.error("儲存交易資料時發生錯誤: {}", transaction.getTransactionInfo().getTransactionNumber(), e);
                throw e; // 重新拋出異常，讓上層決定如何處理
            }
        }
        log.info("成功儲存 {} 筆交易資料", successCount);
    }
   
    /**
     * 更新交易資料
     */
    public void updateTransaction(String transactionNumber, double newAmount, String newTime) throws SQLException {
        if (exchangeDao.update(transactionNumber, newAmount, newTime)) {
            log.info("成功更新交易資料: {}", transactionNumber);
        } else {
            log.warn("找不到要更新的交易資料: {}", transactionNumber);
        }
    }

    /**
     * 刪除交易資料
     */
    public void deleteTransaction(String transactionNumber) throws SQLException {
        if (exchangeDao.delete(transactionNumber)) {
            log.info("成功刪除交易資料: {}", transactionNumber);
        } else {
            log.warn("找不到要刪除的交易資料: {}", transactionNumber);
        }
    }
}