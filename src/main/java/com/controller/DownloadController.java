package com.controller;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.service.DownloadExchangeService;

import io.jsonwebtoken.io.IOException;

import com.model.transactionInfo.ExchangeTransaction;
@Path("/download")
public class DownloadController {
    // 日誌記錄器，用於記錄系統運行過程中的關鍵事件和錯誤
    private static final Logger log = LogManager.getLogger(DownloadController.class);

    // 服務層實例，負責處理交易相關的業務邏輯
    private final DownloadExchangeService service;

    // 建構子，初始化服務實例
    public DownloadController() {
        this.service = new DownloadExchangeService();
    }

    /**
     * 處理 CSV 檔案下載的 HTTP GET 請求
     * 
     * @return 包含 CSV 檔案的 HTTP 回應
     */
    @GET
    @Path("/csv")
    @Produces("text/csv")
    public Response downloadCsv() {
        // 記錄下載請求的開始
        log.info("開始處理下載 CSV 請求");

        try {
            // 從服務層獲取所有交易資料
            List<ExchangeTransaction> transactions = service.downloadAllTransactions();

            // 建立串流輸出物件，用於逐步寫入 CSV 檔案
            StreamingOutput streamOutput = new StreamingOutput() {
                @Override
                public void write(OutputStream output) throws java.io.IOException  {
                    // 建立寫入器，指定 UTF-8 編碼以支援中文
                    try (Writer writer = new OutputStreamWriter(output, StandardCharsets.UTF_8)) {
                        // 寫入 BOM（Byte Order Mark），解決 Excel 中文亂碼問題
//                        writer.write('\ufeff');

                        // 寫入 CSV 標題列，定義每一欄的名稱
                        writer.write("姓名,身分證字號,生日,國籍,電話,幣別,結匯金額,匯款代碼,交易說明,交易時間,交易序號\n");

                        // 遍歷每筆交易資料，將其寫入 CSV
                        for (ExchangeTransaction tx : transactions) {
                            // 使用 String.format 格式化每筆交易資料
                            // %s 用於字串，%.2f 用於浮點數（保留兩位小數）
                            try {
								writer.write(String.format("%s,%s,%s,%s,%s,%s,%.2f,%s,%s,%s,%s\n", 
								    // 使用 escapeCsv 方法處理特殊字元，確保 CSV 格式正確
								    escapeCsv(tx.getPersonalInfo().getName()),                      // 姓名
								    escapeCsv(tx.getPersonalInfo().getIdNumber()),                 // 身分證字號
								    escapeCsv(tx.getPersonalInfo().getBirthDate()),                // 生日
								    escapeCsv(tx.getPersonalInfo().getNationality()),              // 國籍
								    escapeCsv(tx.getPersonalInfo().getPhoneNumber()),              // 電話
								    escapeCsv(tx.getPersonalInfo().getCurrency()),                 // 幣別
								    tx.getTransactionInfo().getExchangeAmount(),                   // 結匯金額
								    escapeCsv(tx.getTransactionInfo().getRemittanceCode()),        // 匯款代碼
								    escapeCsv(tx.getTransactionInfo().getTransactionDescription()), // 交易說明
								    escapeCsv(tx.getTransactionInfo().getTransactionTime()),       // 交易時間
								    escapeCsv(tx.getTransactionInfo().getTransactionNumber())      // 交易序號
								));
							} catch (java.io.IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                        }
                    }
                }
            };

            // 產生動態檔案名稱，使用當前日期
            String filename = String.format("exchange_transactions_%s.csv", LocalDate.now().toString());

            // 建立 HTTP 回應，設定下載相關的標頭資訊
            return Response.ok(streamOutput)
                .header("Content-Disposition", "attachment; filename=" + filename)
                .header("Content-Type", "text/csv;charset=UTF-8")
                .build();

        } catch (Exception e) {
            // 若發生任何例外，記錄錯誤並回傳伺服器錯誤訊息
            log.error("下載 CSV 時發生錯誤", e);
            return Response.serverError().entity("下載失敗：" + e.getMessage()).build();
        }
    }

    /**
     * CSV 特殊字元轉義方法
     * 處理可能破壞 CSV 結構的特殊字元
     * 
     * @param value 原始字串值
     * @return 轉義後的字串值
     */
    private String escapeCsv(String value) {
        // 如果值為 null，返回空字串
        if (value == null) return "";

        // 如果值包含逗號、引號或換行符
        // 將整個值用引號包圍，並將內部的引號重複一次
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        // 若無特殊字元，直接返回原值
        return value;
    }
}