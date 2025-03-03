package com.controller;

import javax.ws.rs.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.core.Context;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dao.ExchangeTransactionDao;
import com.model.controllerResult.DuplicateCheckResult;
import com.model.controllerResult.ErrorResponse;
import com.model.transactionInfo.ExchangeTransaction;
import com.service.DuplicateCheckService;



import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
//檢查上傳資料是否有重複
@Path("/check")
public class DuplicateCheckController {
	private final DuplicateCheckService duplicateCheckService;
	private final ExchangeTransactionDao exchangeTransactionDao;
	
	private static final Logger log = LogManager.getLogger(DuplicateCheckController.class);


	
	public DuplicateCheckController() {
		this.duplicateCheckService = new DuplicateCheckService();
		this.exchangeTransactionDao =new ExchangeTransactionDao();
	}

	@POST
	@Path("/duplicates")
	@Consumes(MediaType.APPLICATION_JSON+ ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON+ ";charset=UTF-8")
	public Response checkDuplicates(List<ExchangeTransaction> transactions) {
		
//		System.out.println("duplicateCheckService"+duplicateCheckService);
		try {
			
			log.info("=== Controller 層接收資料階段 ===");
	        if (!transactions.isEmpty()) {
	            ExchangeTransaction firstTrans = transactions.get(0);
	            log.info("第一筆交易資料狀態：");
	            log.info("交易序號: {}", firstTrans.getTransactionInfo().getTransactionNumber());
	            log.info("姓名: {}", firstTrans.getPersonalInfo().getName());
	            log.info("國籍: {}", firstTrans.getPersonalInfo().getNationality());
	            log.info("交易描述: {}", firstTrans.getTransactionInfo().getTransactionDescription());
	        }
	        log.info("總筆數: {}", transactions.size());
			
			
			
			// 步驟 1: 呼叫 DuplicateCheckService 取得重複交易清單
	        log.info("=== 準備呼叫 DuplicateCheckService ===");
			List<ExchangeTransaction> duplicates = duplicateCheckService.checkDuplicates(transactions);

			log.info("=== Service 處理完成 ===");
	        if (!duplicates.isEmpty()) {
	            ExchangeTransaction firstDup = duplicates.get(0);
	            log.info("第一筆重複交易資料：");
	            log.info("交易序號: {}", firstDup.getTransactionInfo().getTransactionNumber());
	            log.info("姓名: {}", firstDup.getPersonalInfo().getName());
	            log.info("國籍: {}", firstDup.getPersonalInfo().getNationality());
	        }
			
			// 步驟 2: 建立新的清單,並從原始清單中移除-重複交易,以獲取非重複交易
			List<ExchangeTransaction> nonDuplicates = new ArrayList<>(transactions);
			nonDuplicates.removeAll(duplicates);

			// 步驟 3: 建立結果物件,包含重複和非重複交易清單,以及各自的筆數
			DuplicateCheckResult result = new DuplicateCheckResult();
			result.setDuplicates(duplicates);
			result.setNonDuplicates(nonDuplicates);
			log.info("duplicates.size() {}", duplicates.size());

			
			result.setDuplicateCount(duplicates.size());
			result.setNonDuplicateCount(nonDuplicates.size());

			// 在回傳結果前記錄
	        log.info("=== Controller 準備回傳結果 ===");
	        if (!duplicates.isEmpty()) {
	            ExchangeTransaction firstResult = duplicates.get(0);
	            log.info("回傳結果中的第一筆資料：");
	            log.info("姓名: {}", firstResult.getPersonalInfo().getName());
	            log.info("國籍: {}", firstResult.getPersonalInfo().getNationality());
	        }
			
			
			// 步驟 4: 將結果以 JSON 格式返回
			return Response.ok(result).build();
		} catch (SQLException e) {
			// 如果發生異常,返回錯誤響應
			e.printStackTrace();
			return createErrorResponse("重複交易檢查失敗: " + e.getMessage());
		
		}
		
	}

	/**
	 * 建立錯誤響應的助手方法,附帶指定的錯誤訊息。
	 *
	 * @param message 要包含在響應中的錯誤訊息。
	 * @return 錯誤響應。
	 */
	private Response createErrorResponse(String message) {
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ErrorResponse(message)).build();
	}
}


