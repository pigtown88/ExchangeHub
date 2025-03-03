package com.controller;

import java.sql.SQLException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.service.PersistenceService;
import com.dao.ExchangeTransactionDao;
import com.dto.UploadStatus;
import com.model.controllerResult.ErrorResponse;

@Path("/exchange/check")
public class CheckUploadStatusController {
    private static final Logger log = LogManager.getLogger(CheckUploadStatusController.class);
    private final PersistenceService persistenceService;

    public CheckUploadStatusController() {
        // 初始化相依的服務
        ExchangeTransactionDao exchangeDao = new ExchangeTransactionDao();
        this.persistenceService = new PersistenceService(exchangeDao);
    }
//測試說今天有沒有上傳過檔案了
    @GET
    @Path("/upload-status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkTodayStatus() {
        try {
            // 使用 PersistenceService 檢查狀態
            UploadStatus status = persistenceService.checkTodayUploadStatus();
            
            // 記錄日誌
            log.info("成功檢查上傳狀態: hasUploaded={}, lastUploadTime={}", 
                    status.isHasUploaded(), status.getLastUploadTime());
            
            return Response.ok(status).build();
            
        } catch (SQLException e) {
            log.error("檢查上傳狀態失敗", e);
            
            ErrorResponse error = new ErrorResponse(
                "檢查上傳狀態失敗: " + e.getMessage()
            );
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity(error)
                         .build();
        } catch (Exception e) {
            log.error("發生未預期的錯誤", e);
            
            ErrorResponse error = new ErrorResponse(
                "系統發生未預期的錯誤"
            );
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity(error)
                         .build();
        }
    }
}