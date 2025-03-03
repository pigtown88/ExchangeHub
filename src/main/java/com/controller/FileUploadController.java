package com.controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.core.Context;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.model.controllerResult.UploadResult;
import com.model.transactionInfo.ExchangeTransaction;
import com.service.CSVParsingService;
//CSV檔案上傳測試
@Path("/exchange")
public class FileUploadController {
    private static final Logger log = LogManager.getLogger(FileUploadController.class);
    private final CSVParsingService csvParsingService;
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    public FileUploadController() {
        this.csvParsingService = new CSVParsingService();
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadFile(@Context HttpServletRequest request) {
        InputStream fileContent = null;
        try {
            log.info("開始處理檔案上傳請求");
            
            // 1. 先取得所有 parts，檢查內容
            for (Part part : request.getParts()) {
                log.info("Part name: {}, size: {}, content type: {}", 
                    part.getName(), part.getSize(), part.getContentType());
            }
            
            // 2. 取得檔案
            Part filePart = request.getPart("file");  // 確保這裡的 "file" 與前端對應
            if (filePart == null) {
                log.warn("檔案部分為空");
                return createErrorResponse("檔案不能為空");
            }

            // 3. 檢查檔案名稱
            String fileName = getFileName(filePart);
            log.info("檔案名稱: {}", fileName);
            if (!fileName.toLowerCase().endsWith(".csv")) {
                log.warn("檔案格式不正確: {}", fileName);
                return createErrorResponse("檔案必須是CSV格式");
            }

            // 4. 檢查檔案大小
            if (filePart.getSize() > MAX_FILE_SIZE) {
                log.warn("檔案太大: {} bytes", filePart.getSize());
                return createErrorResponse("檔案大小不能超過10MB");
            }

            // 5. 讀取檔案內容
            fileContent = filePart.getInputStream();
            // 將輸入流的內容轉換為字串，這樣可以在日誌中看到實際內容
            String fileString = IOUtils.toString(fileContent, StandardCharsets.UTF_8);
            log.info("檔案內容前500字節: {}", fileString.substring(0, Math.min(500, fileString.length())));
            
            // 6. 重新將字串轉換為輸入流進行解析
            List<ExchangeTransaction> transactions = csvParsingService.parseCSV(
                IOUtils.toInputStream(fileString, StandardCharsets.UTF_8));
            
            log.info("解析完成，共有{}筆交易記錄", transactions.size());

            // 7. 建立回應
            UploadResult result = new UploadResult();
            result.setSuccess(true);
            result.setMessage("檔案上傳成功");
            result.setFileName(fileName);
            result.setRecordCount(transactions.size());
            result.setTransactions(transactions);

            log.info("檔案處理完成，準備返回結果");
            return Response.ok(result).build();

        } catch (Exception e) {
            log.error("檔案上傳失敗", e);
            return createErrorResponse("檔案上傳失敗: " + e.getMessage());
        } finally {
            // 確保關閉資源
            if (fileContent != null) {
                try {
                    fileContent.close();
                } catch (Exception e) {
                    log.error("關閉檔案流時發生錯誤", e);
                }
            }
        }
    }

    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] elements = contentDisposition.split(";");
        for (String element : elements) {
            if (element.trim().startsWith("filename")) {
                return element.substring(element.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return "";
    }

    private Response createErrorResponse(String message) {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(new UploadResult(message))
            .build();
    }
}