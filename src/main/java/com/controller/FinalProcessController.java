package com.controller;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dao.ExchangeTransactionDao;
import com.model.*;
import com.model.controllerResult.ErrorResponse;
import com.model.threeInOneEncryption.ProcessRequest;
import com.model.threeInOneEncryption.ProcessResult;
import com.service.FinalProcessUploadService;
//確認每筆非重複交易
@Path("/exchange/process")
public class FinalProcessController {
    private static final Logger log = LogManager.getLogger(FinalProcessController.class);
    private final FinalProcessUploadService finalProcessUploadService;

    public FinalProcessController() {
        ExchangeTransactionDao exchangeDao = new ExchangeTransactionDao();
        this.finalProcessUploadService = new FinalProcessUploadService(exchangeDao);
    }

    @POST
    @Path("/final")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response processFinalUpload(ProcessRequest request, @Context HttpServletRequest httpRequest, @Context HttpServletResponse httpResponse) {
        try {
            // 設置請求和響應編碼
            httpRequest.setCharacterEncoding("UTF-8");
            httpResponse.setCharacterEncoding("UTF-8");

            log.info("收到請求的數據: {}", request);

            // 請求資料驗證
            if (request == null || request.getNonDuplicates() == null 
                    || request.getNonDuplicates().isEmpty()) {
                log.warn("請求資料不完整");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("請求資料不完整"))
                        .build();
            }

            // 處理上傳
            ProcessResult result = finalProcessUploadService.checkTodayStatusAndInsert(
                request.getNonDuplicates(), 
                request.getDuplicates()
            );

            log.info("處理完成，成功筆數: {}", result.getProcessedCount());
            return Response.ok(result).build();

        } catch (Exception e) {
            log.error("處理最終上傳時發生錯誤", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("資料處理失敗: " + e.getMessage()))
                    .build();
        }
    }
}




