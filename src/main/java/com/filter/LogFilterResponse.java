package com.filter;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.logging.log4j.Logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;


/**
* 這個過濾器用於記錄所有離開應用程序的HTTP響應的詳細信息。
* 它捕獲響應的內容和處理時間,並將這些信息記錄下來。
*/
@WebFilter("/*")
public class LogFilterResponse implements Filter {
    // 創建日誌記錄器實例
    private static final Logger logger = LogManager.getLogger(LogFilterResponse.class);

    /**
     * 初始化方法，在過濾器首次創建時調用
     */
    @Override
    public void init(FilterConfig filterConfig) {
        // 初始化邏輯，如果需要的話可以在這裡添加
    }

    /**
     * doFilter 方法，處理每個通過此過濾器的請求和響應
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
    	//需要記錄到HttpServletRequest 是因為我需要去紀錄整個過程的響應時間(結尾時間-開頭時間)
    	HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    	
    	
        // 將通用的ServletRequest和ServletResponse轉換為HTTP特定的版本
    	//進行格式的轉換由 ServletResponse response 轉換成 HttpServletResponse httpServletResponse
    	//WHY需要由HttpServletResponse轉換成為HttpServletResponse?
    	//一、因為可以調用http的getHeader、getMethod方法 二、HTTP可以告訴我更多訊息 
    	
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        // 創建自定義響應包裝器，用於捕獲響應內容
        ResponseWrapper wrappedResponse = new ResponseWrapper(httpServletResponse);

        String trackId = ThreadContext.get("trackId");
        logger.info("當前處理的請求 trackId:{}", trackId);
        
        // 記錄請求處理的開始時間
        long startTime = System.currentTimeMillis();
        logger.info("開始記錄請求處理時間");
        try {
            // 將請求傳遞給過濾器鏈中的下一個元素
            chain.doFilter(request, wrappedResponse);
        } finally {
            // 計算請求處理的持續時間
            long duration = System.currentTimeMillis() - startTime;
            logger.info("開始計算請求處理時間");
            // 獲取請求的URL
            String requestURL = httpServletRequest.getRequestURL().toString();
            // 獲取響應體的內容
            String responseBody = wrappedResponse.getCaptureAsString();

            // 記錄請求處理完成的信息，包括URL、處理時間和響應
            logger.info("\n 請求處理完畢"
                    + " - URL: {}, 處理時間: {} 毫秒 " + "\n ResponseBody: {} "
                    + "\n------------------------結束------------------------"
                    , requestURL, duration, responseBody);

            // 如果響應尚未提交，將捕獲的響應數據寫回客戶端
            if (!response.isCommitted()) {
                byte[] capturedData = wrappedResponse.getCaptureAsBytes();
                ServletOutputStream output = response.getOutputStream();
                output.write(capturedData);
                output.flush();
            }

            // 從當前線程的上下文中移除唯一ID
            ThreadContext.remove("trackId");
            logger.info("LogFilterResponse已經丟棄trackId");
            
            
            logger.info("LogFilterRequest已經丟棄trackId");
        }
    }

  
}