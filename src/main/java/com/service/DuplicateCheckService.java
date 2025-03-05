package com.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dao.ExchangeTransactionDao;
import com.model.transactionInfo.ExchangeTransaction;
import com.model.transactionInfo.PersonalInfo;
import com.model.transactionInfo.TransactionInfo;

/**
 * 重複交易檢查服務 負責檢查新交易是否與資料庫中的歷史交易重複
 */
public class DuplicateCheckService {
	private static final Logger log = LogManager.getLogger(DuplicateCheckService.class);
	private final ExchangeTransactionDao exchangeDao;

	public DuplicateCheckService() {
		this.exchangeDao = new ExchangeTransactionDao();
	}

	/**
	 * 批量檢查重複交易 檢查條件：交易序號、身分證字號、金額三者都相同視為重複
	 * 
	 * @param transactions 要檢查的交易列表
	 * @return 重複的交易列表
	 * @throws SQLException 當資料庫操作發生錯誤時拋出
	 */
	public List<ExchangeTransaction> checkDuplicates(List<ExchangeTransaction> transactions) throws SQLException {
		List<ExchangeTransaction> duplicates = new ArrayList<>();

		log.info("=== Service 層開始處理資料 ===");
		if (!transactions.isEmpty()) {
			ExchangeTransaction firstTrans = transactions.get(0);
			log.info("收到的第一筆交易資料：");
			log.info("交易序號: {}", firstTrans.getTransactionInfo().getTransactionNumber());
			log.info("姓名: {}", firstTrans.getPersonalInfo().getName());
			log.info("國籍: {}", firstTrans.getPersonalInfo().getNationality());
		}
		for (ExchangeTransaction transaction : transactions) {
//			// 原因：追蹤交易資料在處理前的編碼狀態
//			logTransactionEncoding("檢查前", transaction);
			log.info("正在檢查交易：{}", transaction.getTransactionInfo().getTransactionNumber());
			log.info("檢查前資料狀態：");
			log.info("姓名: {}", transaction.getPersonalInfo().getName());
			if (isDuplicate(transaction)) {
				// 原因：確保重複交易的中文資料編碼正確
				log.info("發現重複交易: {} {} {}", transaction.getTransactionInfo().getTransactionNumber(),
						transaction.getPersonalInfo().getIdNumber(),
						transaction.getTransactionInfo().getExchangeAmount());
				// 加入重複清單前記錄
				log.info("加入重複清單前的資料狀態：");
				log.info("姓名: {}", transaction.getPersonalInfo().getName());
				log.info("國籍: {}", transaction.getPersonalInfo().getNationality());

				duplicates.add(transaction);

//				// 原因：確認編碼轉換的結果
			}

		}
		// 回傳前記錄
		log.info("=== Service 層處理完成 ===");
		if (!duplicates.isEmpty()) {
			ExchangeTransaction firstDup = duplicates.get(0);
			log.info("準備回傳的第一筆重複交易：");
			log.info("姓名: {}", firstDup.getPersonalInfo().getName());
			log.info("國籍: {}", firstDup.getPersonalInfo().getNationality());
		}

		return duplicates;
	}

	/**
	 * 檢查單筆交易是否重複
	 * 
	 * @param transaction 要檢查的交易
	 * @return true如果是重複交易，否則返回false
	 * @throws SQLException 當資料庫操作發生錯誤時拋出
	 */
	private boolean isDuplicate(ExchangeTransaction transaction) throws SQLException {
		return exchangeDao.checkDuplicate(transaction.getTransactionInfo().getTransactionNumber(),
				transaction.getPersonalInfo().getIdNumber(), transaction.getTransactionInfo().getExchangeAmount());
	}

}