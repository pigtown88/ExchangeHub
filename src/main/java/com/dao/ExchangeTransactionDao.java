package com.dao;

import java.security.Timestamp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.model.transactionInfo.ExchangeTransaction;
import com.model.transactionInfo.PersonalInfo;
import com.model.transactionInfo.TransactionInfo;

//ExchangeTransactionDao.java
public class ExchangeTransactionDao {

	private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private static final String URL = "jdbc:sqlserver://172.16.46.181:1433;databaseName=ExchangeHub;"
			+ "encrypt=true;trustServerCertificate=true;characterEncoding=UTF-8;";
	private static final String USER = "sa";
	private static final String PASSWORD = "Passw0rd";

	private static final Logger log = LogManager.getLogger(ExchangeTransactionDao.class);

//測試的到紀夫DB連線
// private static final String URL = "jdbc:sqlserver://172.16.45.213:1433;databaseName=TEST;encrypt=true;trustServerCertificate=true;characterEncoding=UTF-8;";
// private static final String USER = "sqlap";
// private static final String PASSWORD = "Ubot@1234";
// 
//靜態區塊載入 SQL Server JDBC 驅動
	static {
		try {
			log.info("開始載入 JDBC 驅動程式: {}", DRIVER);
			Class.forName(DRIVER);
			log.info("JDBC 驅動程式載入成功");
		} catch (ClassNotFoundException e) {
			log.error("載入 JDBC 驅動程式失敗", e);
			throw new RuntimeException("Failed to load SQL Server JDBC driver", e);
		}
	}

	// SQL語句
	private static final String INSERT_SQL = """
			INSERT INTO exchange_transactions1 (
			    name, id_number, birth_date, nationality,
			    phone_number, currency, exchange_amount,
			    remittance_code, transaction_description,
			    transaction_time, transaction_number
			) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
			""";

	private static final String UPDATE_SQL = """
			UPDATE exchange_transactions1
			SET exchange_amount = ?,
			    transaction_time = ?
			WHERE transaction_number = ?
			""";

	private static final String DELETE_SQL = "DELETE FROM exchange_transactions1 WHERE transaction_number = ?";

	private static final String SELECT_SQL = "SELECT * FROM exchange_transactions1 WHERE transaction_number = ?";

	private static final String CHECK_DUPLICATE_SQL = """
			SELECT COUNT(1) FROM exchange_transactions1
			WHERE transaction_number = ?
			AND id_number = ?
			AND exchange_amount = ?
			""";


	public String getLastUploadTimeForDate(LocalDate date) throws SQLException {
		String sql = """
				SELECT MAX(updated_at) as last_upload_time
				FROM exchange_transactions1
				WHERE CAST(updated_at AS DATE) = ?
												""";

		log.info("開始查詢日期 {} 的最後上傳時間", date);
		log.debug("SQL查詢語句: {}", sql);

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
			log.debug("資料庫連線建立成功");

			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setObject(1, date);
				log.debug("SQL參數設置完成，date={}", date);

				try (ResultSet rs = pstmt.executeQuery()) {
					log.debug("SQL查詢執行完成");

					if (rs.next()) {
						java.sql.Timestamp lastUploadTime = rs.getTimestamp("last_upload_time");
						log.info("查詢結果: lastUploadTime={}", lastUploadTime);

						String result = rs.getString("last_upload_time");
						log.info("查詢結果: lastUploadTime={}", result);
						return result;
					}
					log.info("未找到上傳記錄");
					return null;
				}
			}
		} catch (SQLException e) {
			log.error("查詢最後上傳時間時發生錯誤 - SQL State: {}, Error Code: {}", e.getSQLState(), e.getErrorCode(), e);
			throw e;
		}
	}

	// 新增交易
	public boolean insert(ExchangeTransaction transaction) throws SQLException {
		log.info("開始新增交易記錄，交易序號: {}", transaction.getTransactionInfo().getTransactionNumber());

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
			log.debug("資料庫連線建立成功");

			try (PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL)) {
				log.debug("準備設置交易參數");

				// 設置個人基本資料
				PersonalInfo personalInfo = transaction.getPersonalInfo();
				pstmt.setString(1, personalInfo.getName());
				pstmt.setString(2, personalInfo.getIdNumber());
				pstmt.setString(3, personalInfo.getBirthDate());
				pstmt.setString(4, personalInfo.getNationality());
				pstmt.setString(5, personalInfo.getPhoneNumber());
				pstmt.setString(6, personalInfo.getCurrency());

				// 設置交易資訊
				TransactionInfo transactionInfo = transaction.getTransactionInfo();
				pstmt.setDouble(7, transactionInfo.getExchangeAmount());
				pstmt.setString(8, transactionInfo.getRemittanceCode());
				pstmt.setString(9, transactionInfo.getTransactionDescription());
				pstmt.setString(10, transactionInfo.getTransactionTime());
				pstmt.setString(11, transactionInfo.getTransactionNumber());

				log.debug("參數設置完成，準備執行SQL");
				int result = pstmt.executeUpdate();
				log.info("新增交易結果: {}, 影響筆數: {}", result > 0 ? "成功" : "失敗", result);
				return result > 0;
			}
		} catch (SQLException e) {
			log.error("新增交易時發生錯誤 - 交易序號: {}, SQL State: {}, Error Code: {}",
					transaction.getTransactionInfo().getTransactionNumber(), e.getSQLState(), e.getErrorCode(), e);
			throw e;
		}
	}

	// 更新交易
	public boolean update(String transactionNumber, double newAmount, String newTime) throws SQLException {
		log.info("開始更新交易 - 交易序號: {}, 新金額: {}, 新時間: {}", transactionNumber, newAmount, newTime);

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
			log.debug("資料庫連線建立成功");

			try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {
				pstmt.setDouble(1, newAmount);
				pstmt.setString(2, newTime);
				pstmt.setString(3, transactionNumber);

				log.debug("參數設置完成，準備執行SQL");
				int result = pstmt.executeUpdate();
				log.info("更新交易結果: {}, 影響筆數: {}", result > 0 ? "成功" : "失敗", result);
				return result > 0;
			}
		} catch (SQLException e) {
			log.error("更新交易時發生錯誤 - 交易序號: {}, SQL State: {}, Error Code: {}", transactionNumber, e.getSQLState(),
					e.getErrorCode(), e);
			throw e;
		}
	}

	// 刪除交易
	public boolean delete(String transactionNumber) throws SQLException {
		log.info("開始刪除交易 - 交易序號: {}", transactionNumber);

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
			log.debug("資料庫連線建立成功");

			try (PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL)) {
				pstmt.setString(1, transactionNumber);

				log.debug("參數設置完成，準備執行SQL");
				int result = pstmt.executeUpdate();
				log.info("刪除交易結果: {}, 影響筆數: {}", result > 0 ? "成功" : "失敗", result);
				return result > 0;
			}
		} catch (SQLException e) {
			log.error("刪除交易時發生錯誤 - 交易序號: {}, SQL State: {}, Error Code: {}", transactionNumber, e.getSQLState(),
					e.getErrorCode(), e);
			throw e;
		}
	}

	// 查詢單筆交易
	public ExchangeTransaction findByTransactionNumber(String transactionNumber) throws SQLException {
		log.info("開始查詢交易 - 交易序號: {}", transactionNumber);

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
			log.debug("資料庫連線建立成功");

			try (PreparedStatement pstmt = conn.prepareStatement(SELECT_SQL)) {
				pstmt.setString(1, transactionNumber);
				log.debug("參數設置完成，準備執行SQL");

				try (ResultSet rs = pstmt.executeQuery()) {
					log.debug("SQL查詢執行完成");
					if (rs.next()) {
						ExchangeTransaction result = mapResultSetToTransaction(rs);
						log.info("查詢到交易記錄");
						return result;
					}
					log.info("未找到交易記錄");
					return null;
				}
			}
		} catch (SQLException e) {
			log.error("查詢交易時發生錯誤 - 交易序號: {}, SQL State: {}, Error Code: {}", transactionNumber, e.getSQLState(),
					e.getErrorCode(), e);
			throw e;
		}
	}

	// 檢查重複交易
	public boolean checkDuplicate(String transactionNumber, String idNumber, Double amount) throws SQLException {
		log.info("開始檢查重複交易 - 交易序號: {}, 身分證字號: {}, 金額: {}", transactionNumber, idNumber, amount);

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
			log.debug("資料庫連線建立成功");

			try (PreparedStatement pstmt = conn.prepareStatement(CHECK_DUPLICATE_SQL)) {
				pstmt.setString(1, transactionNumber);
				pstmt.setString(2, idNumber);
				pstmt.setDouble(3, amount);

				log.debug("參數設置完成，準備執行SQL");
				try (ResultSet rs = pstmt.executeQuery()) {
					boolean isDuplicate = rs.next() && rs.getInt(1) > 0;
					log.info("重複檢查結果: {}", isDuplicate ? "發現重複" : "無重複");
					return isDuplicate;
				}
			}
		} catch (SQLException e) {
			log.error("檢查重複交易時發生錯誤 - 交易序號: {}, SQL State: {}, Error Code: {}", transactionNumber, e.getSQLState(),
					e.getErrorCode(), e);
			throw e;
		}
	}
	
	/**
	 * 查詢所有交易記錄
	 */
	public List<ExchangeTransaction> findAllTransactions() throws SQLException {
	    String sql = "SELECT * FROM exchange_transactions1 ORDER BY updated_at DESC";
	    List<ExchangeTransaction> transactions = new ArrayList<>();
	    
	    log.info("開始查詢所有交易記錄");
	    
	    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
	         PreparedStatement pstmt = conn.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {
	        
	        while (rs.next()) {
	            transactions.add(mapResultSetToTransaction(rs));
	        }
	        
	        log.info("查詢完成，共取得 {} 筆交易記錄", transactions.size());
	        return transactions;
	    } catch (SQLException e) {
	        log.error("查詢所有交易記錄時發生錯誤", e);
	        throw e;
	    }
	}
	
	
	/**
	 * 刪除所有交易記錄
	 */
	public int deleteTodayTransactions() throws SQLException {  
		String sql = "DELETE FROM exchange_transactions1 WHERE CAST(updated_at AS DATE) = CAST(GETDATE() AS DATE)";  
		  
		log.info("開始刪除今日交易記錄");  
		  
		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);  
		PreparedStatement pstmt = conn.prepareStatement(sql)) {  
		  
		int result = pstmt.executeUpdate();  
		log.info("刪除今日交易結果: 影響筆數: {}", result);  
		return result;  
		} catch (SQLException e) {  
		log.error("刪除今日交易記錄時發生錯誤 - SQL State: {}, Error Code: {}",  
		e.getSQLState(), e.getErrorCode(), e);  
		throw e;  
		}  
		}  

	// ResultSet映射到物件
	private ExchangeTransaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
		log.debug("開始將 ResultSet 映射到交易物件");

		ExchangeTransaction transaction = new ExchangeTransaction();

		// 設置個人資訊
		PersonalInfo personalInfo = new PersonalInfo();
		personalInfo.setName(rs.getString("name"));
		personalInfo.setIdNumber(rs.getString("id_number"));
		personalInfo.setBirthDate(rs.getString("birth_date"));
		personalInfo.setNationality(rs.getString("nationality"));
		personalInfo.setPhoneNumber(rs.getString("phone_number"));
		personalInfo.setCurrency(rs.getString("currency"));
		transaction.setPersonalInfo(personalInfo);

		// 設置交易資訊
		TransactionInfo transactionInfo = new TransactionInfo();
		transactionInfo.setExchangeAmount(rs.getDouble("exchange_amount"));
		transactionInfo.setRemittanceCode(rs.getString("remittance_code"));
		transactionInfo.setTransactionDescription(rs.getString("transaction_description"));
		transactionInfo.setTransactionNumber(rs.getString("transaction_number"));
		transactionInfo.setTransactionTime(rs.getString("transaction_time"));
		transaction.setTransactionInfo(transactionInfo);

		log.debug("交易物件映射完成 - 交易序號: {}", transaction.getTransactionInfo().getTransactionNumber());
		return transaction;
	}
}