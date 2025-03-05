package com.service;


import com.dao.ExchangeTransactionDao;
import com.model.transactionInfo.ExchangeTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;

public class DownloadExchangeService {
    private static final Logger log = LogManager.getLogger(DownloadExchangeService.class);
    private final ExchangeTransactionDao dao;

    public DownloadExchangeService() {
        this.dao = new ExchangeTransactionDao();
    }

    /**
     * 下載所有交易記錄為 CSV
     */
    public List<ExchangeTransaction> downloadAllTransactions() throws Exception {
        log.info("開始準備下載所有交易記錄");
        try {
            return dao.findAllTransactions();
        } catch (Exception e) {
            log.error("下載交易記錄時發生錯誤", e);
            throw e;
        }
    }
}