package com.service;

// File: com/exchange/service/csv/CSVParsingService.java

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.model.transactionInfo.ExchangeTransaction;
import com.model.transactionInfo.PersonalInfo;
import com.model.transactionInfo.TransactionInfo;

/*
 * CSV解析服務 負責處理CSV檔案的讀取和解析，將原始數據轉換為領域模型對象
 * 步驟一:讀取並處理標題
 * 步驟二:讀取CSV檔案並轉換成物件
 * 步驟三:驗證並記錄每筆交易資料
 */

public class CSVParsingService {
	private static final Logger log = LogManager.getLogger(CSVParsingService.class);
	private static final int EXPECTED_FIELD_COUNT = 13; // 預設會有13個欄位

	public List<ExchangeTransaction> parseCSV(InputStream inputStream) throws IOException {
		List<ExchangeTransaction> transactions = new ArrayList<>();
		log.info("開始處理CSV檔案解析作業");

		/*
		 * 讀取檔案標題和欄位定義 逐行處理交易結果
		 */
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			// 處理第一行：檔案標題和日期
			String fileHeader = reader.readLine();
			String[] headerParts = fileHeader.split(",");
			log.info("檔案資訊 - 類型: {}, 日期: {}", headerParts[0], headerParts[1]);

			// 處理第二行：欄位標題
			String columnHeader = reader.readLine();
			log.info("欄位標題: {}", columnHeader);

			// 處理資料行
			String line;
			int lineNumber = 0;

			log.info("開始處理逐行交易資料");
			while ((line = reader.readLine()) != null) {
				lineNumber++;
				log.info("正在處理{}行交易資料", lineNumber);
				try {
					ExchangeTransaction transaction = parseTransactionData(line, lineNumber);
					if (transaction != null) {
						transactions.add(transaction);
					}
				} catch (Exception e) {
					log.error("第{}行資料處理失敗，原始資料: {}, 錯誤訊息: {}", lineNumber, line, e.getMessage());
				}
			}

			log.info("CSV檔案解析完成，共處理{}筆資料，成功解析{}筆", lineNumber, transactions.size());
		}

		return transactions;
	}

	/*
	 * 目的:解析單筆資料
	 * 步驟一:分割資料欄位
	 * 步驟二:建立交易物件
	 * 步驟三:處理結果並記錄
	 */
	private ExchangeTransaction parseTransactionData(String line, int lineNumber) {
		// 步驟一:分割資料欄位
		String[] fields = line.split(",");

		if (fields.length != EXPECTED_FIELD_COUNT) {
			log.warn("第{}行資料欄位數量不符，預期{}個但實際有{}個", lineNumber, EXPECTED_FIELD_COUNT, fields.length);
			return null;
		}

		try {
			// 步驟二:建立交易物件
			ExchangeTransaction transaction = new ExchangeTransaction();
            //步驟二-1 設置個人資訊
			// 設置個人資訊
			PersonalInfo personalInfo = new PersonalInfo();
			personalInfo.setName(fields[0]);
			personalInfo.setIdNumber(fields[1]);
			personalInfo.setBirthDate(fields[2]);
			personalInfo.setNationality(fields[3]);
			personalInfo.setResidencePermitIssueDate(fields[4]);
			personalInfo.setResidencePermitExpiryDate(fields[5]);
			personalInfo.setPhoneNumber(fields[6]);
			personalInfo.setCurrency(fields[7]);
			transaction.setPersonalInfo(personalInfo);
            //步驟二- 設置交易資訊
			// 設置交易資訊
			TransactionInfo transactionInfo = new TransactionInfo();
			transactionInfo.setExchangeAmount(parseAmount(fields[8]));
			transactionInfo.setRemittanceCode(fields[9]);
			transactionInfo.setTransactionDescription(fields[10]);
			transactionInfo.setTransactionTime(fields[11]);
			transactionInfo.setTransactionNumber(fields[12]);
			transaction.setTransactionInfo(transactionInfo);

			logTransactionSuccess(lineNumber, transaction);
			return transaction;

		} catch (Exception e) {
			log.error("第{}行資料解析失敗: {}", lineNumber, e.getMessage());
			return null;
		}
	}

	private double parseAmount(String amount) {
		try {
			return Double.parseDouble(amount.replaceAll("[^\\d.]", ""));
		} catch (NumberFormatException e) {
			log.error("金額格式轉換失敗: {}", amount);
			throw e;
		}
	}

	private void logTransactionSuccess(int lineNumber, ExchangeTransaction transaction) {
		PersonalInfo personalInfo = transaction.getPersonalInfo();
		TransactionInfo transactionInfo = transaction.getTransactionInfo();

		log.info(
				"\n========== 第{}筆交易資料詳細內容 ==========\n" + "【個人基本資料】\n" + "姓名: {}\n" + "身分證字號: {}\n" + "生日: {}\n"
						+ "國籍: {}\n" + "居留證核發日期: {}\n" + "居留證有效期限: {}\n" + "電話: {}\n" + "幣別: {}\n" + "\n" + "【交易資訊】\n"
						+ "交易序號: {}\n" + "結匯金額: {}\n" + "匯款性質分類編號: {}\n" + "交易商品描述: {}\n" + "交易時間: {}\n"
						+ "==========================================",
				lineNumber,
				// 個人資訊
				personalInfo.getName(), personalInfo.getIdNumber(), personalInfo.getBirthDate(),
				personalInfo.getNationality(), personalInfo.getResidencePermitIssueDate(),
				personalInfo.getResidencePermitExpiryDate(), personalInfo.getPhoneNumber(), personalInfo.getCurrency(),
				// 交易資訊
				transactionInfo.getTransactionNumber(), transactionInfo.getExchangeAmount(),
				transactionInfo.getRemittanceCode(), transactionInfo.getTransactionDescription(),
				transactionInfo.getTransactionTime());

		// 添加資料驗證檢查結果
		log.debug("\n---------- 資料驗證結果 ----------");
		validateAndLogField("姓名", personalInfo.getName());
		validateAndLogField("身分證號", personalInfo.getIdNumber());
		validateAndLogField("結匯金額", String.valueOf(transactionInfo.getExchangeAmount()));
		validateAndLogField("交易序號", transactionInfo.getTransactionNumber());
		validateAndLogField("交易時間", transactionInfo.getTransactionTime());
		log.debug("----------------------------------");
	}

	private void validateAndLogField(String fieldName, String value) {
		if (value == null || value.trim().isEmpty()) {
			log.warn("欄位 [{}] 為空值", fieldName);
		} else if (value.contains("null")) {
			log.warn("欄位 [{}] 包含 'null' 字串: {}", fieldName, value);
		} else {
			log.debug("欄位 [{}] 驗證通過: {}", fieldName, value);
		}
	}
}