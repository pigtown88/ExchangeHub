// LogFilterRequest.java
package com.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.UUID;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;
import org.glassfish.jersey.servlet.WebConfig.ConfigType;


@WebFilter("/*")
public class LogFilterRequest implements Filter {
    private static final Logger logger = LogManager.getLogger(LogFilterRequest.class);

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
    	
    	//進行格式的轉換由 ServletRequest request 轉換成 HttpServletRequest httpServletRequest
    	//為什麼需要由ServletRequest轉換成為HttpServletRequest
    	//一、因為可以調用http的getHeader、getMethod方法 二、HTTP可以告訴我更多訊息 
    	HttpServletRequest httpServletRequest = (HttpServletRequest)request;
    	
    	String contentType = httpServletRequest.getContentType();
    	
    	if (contentType != null && contentType.contains("multipart/form-data")) {
			//對於檔案上傳的請求，做特殊的處理
    		chain.doFilter(request, response);
    		return;
		}
    	
    	//創造了一個自訂義的包裝器RequestWrapper繼承自httpServletRequest
    	//為什麼需要去包裝呢? 因為裡面有一些方法我需要修改(EX 緩存請求體，因為普通只能夠存取一次)
    	RequestWrapper wrappedRequest = new RequestWrapper(httpServletRequest);
    	

    	
//       // 把UUID放到threadContext裡面
        String trackId = UUID.randomUUID().toString();
        ThreadContext.put("trackId", trackId);
        logger.info("已經會放trackId進ThreadContext裡面");


        // 記錄請求信息
        String requestURL = wrappedRequest.getRequestURL().toString();
        String queryString = wrappedRequest.getQueryString();
        String method = wrappedRequest.getMethod();
        String remoteAddr = wrappedRequest.getRemoteAddr();
        
        String body = wrappedRequest.getBody();
        StringBuilder headers = new StringBuilder();
        logger.info("已經紀錄request header");
        
        //捕獲多個header的方法
        Enumeration<String> headerNames = wrappedRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = wrappedRequest.getHeader(headerName);
            headers.append(headerName).append(": ").append(headerValue).append(", ");
        }

        logger.info("\n------------------------開始------------------------\n" +
                    "收到請求(Request) - trackId: {}\n" +
                    "URL: {}\n" +
                    "Query: {}\n" +
                    "Method: {}\n" +
                    "IP: {}\n" +
                    "Headers: {}\n" +
                    "RequestBody: {}\n" +
                    "********************************",
                    trackId, requestURL, queryString, method, remoteAddr, headers, body);

        chain.doFilter(wrappedRequest, response);
        ThreadContext.remove("uniqueID");
        
    }

  
    @Override
    public void destroy() {}
}