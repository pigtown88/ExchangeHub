package com.service.test;


import com.model.transactionInfo.ExchangeTransaction;
import com.service.DownloadExchangeService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DownloadExchangeServiceTest {
    private static final Logger log = LogManager.getLogger(DownloadExchangeServiceTest.class);
    
    public static void main(String[] args) {
        testDownloadTransactions();
    }
    
    private static void testDownloadTransactions() {
        log.info("開始測試下載交易記錄功能");
        DownloadExchangeService service = new DownloadExchangeService();
        
        try {
            // 取得所有交易記錄
            List<ExchangeTransaction> transactions = service.downloadAllTransactions();
            
            // 輸出查詢結果
            log.info("成功取得交易記錄，共 {} 筆資料", transactions.size());
            
            // 顯示前5筆資料的內容
            log.info("前5筆資料內容：");
            transactions.stream()
                .limit(5)
                .forEach(tx -> {
                    log.info("====================================");
                    log.info("交易序號: {}", tx.getTransactionInfo().getTransactionNumber());
                    log.info("姓名: {}", tx.getPersonalInfo().getName());
                    log.info("身分證字號: {}", tx.getPersonalInfo().getIdNumber());
                    log.info("交易金額: {}", tx.getTransactionInfo().getExchangeAmount());
                    log.info("交易時間: {}", tx.getTransactionInfo().getTransactionTime());
                    log.info("====================================");
                });

                
        } catch (Exception e) {
            log.error("測試過程中發生錯誤", e);
            e.printStackTrace();
        }
    }
}