package com.filter.token;

import javax.annotation.security.PermitAll;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.service.AuthenticationService;
import com.service.response.ServiceResult;
import com.utils.jwt.JwtUtil;
import com.exception.ErrorCodes;
import com.model.member.AuthErrorResponse;

@Provider
public class JwtAuthenticationFilter implements ContainerRequestFilter {
    private static final Logger logger = LogManager.getLogger(JwtAuthenticationFilter.class);
    private final AuthenticationService authService;
    private final JwtUtil jwtUtil;
    
    @Context
    private ResourceInfo resourceInfo;
    
    public JwtAuthenticationFilter() {
        this.authService = new AuthenticationService();
        this.jwtUtil = new JwtUtil(); //已經新增一個空的建構子
    }
    
    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (isPermittedResource()) {
            logger.debug("檢測到公開訪問資源，允許請求通過");
            return;
        }
        
        logger.debug("開始處理需要Token驗證的請求");
        String authHeader = requestContext.getHeaderString("Authorization");
        
        // 驗證認證頭是否存在
        if (authHeader == null) {
            logger.warn("請求未包含認證頭");
            abortWithUnauthorized(requestContext,
                ErrorCodes.AUTH_HEADER_MISSING,
                "請求未包含認證頭"
            );
            return;
        }
        
        // 驗證認證頭格式
        if (!authHeader.startsWith("Bearer ")) {
            logger.warn("認證頭格式不正確: {}", authHeader);
            abortWithUnauthorized(requestContext,
                ErrorCodes.AUTH_FORMAT_INVALID,
                "認證頭格式不正確，應以'Bearer '開頭"
            );
            return;
        }
        
        // 提取並驗證Token
        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) {
            logger.warn("Token為空");
            abortWithUnauthorized(requestContext,
                ErrorCodes.TOKEN_EMPTY,
                "提供的Token為空"
            );
            return;
        }
        
        try {
            // 檢查Token是否過期
            if (jwtUtil.isTokenExpired(token)) {
                logger.warn("Token已過期");
                abortWithUnauthorized(requestContext,
                    ErrorCodes.TOKEN_EXPIRED,
                    "Token已過期，請重新登入"
                );
                return;
            }
            
            // 驗證Token有效性
            ServiceResult<Boolean> result = authService.validateToken(token);
            if (!result.isSuccess() || !result.getData()) {
                logger.warn("Token驗證失敗");
                abortWithUnauthorized(requestContext,
                    ErrorCodes.TOKEN_INVALID,
                    "Token驗證失敗，可能是無效或已被撤銷"
                );
                return;
            }
            
            logger.debug("Token驗證成功");
            
        } catch (Exception e) {
            logger.error("Token驗證過程發生異常", e);
            abortWithUnauthorized(requestContext,
                ErrorCodes.TOKEN_VALIDATE_ERROR,
                "Token驗證過程發生錯誤: " + e.getMessage()
            );
        
            
        }
    }
    
    private boolean isPermittedResource() {
        return resourceInfo.getResourceMethod().isAnnotationPresent(PermitAll.class) ||
               resourceInfo.getResourceClass().isAnnotationPresent(PermitAll.class);
    }
    
    private void abortWithUnauthorized(
            ContainerRequestContext requestContext,
            String code,
            String message) {
        
        AuthErrorResponse error = new AuthErrorResponse(code, "UNAUTHORIZED", message);
        Response response = Response.status(Response.Status.UNAUTHORIZED)
                .entity(error)
                .build();
                
        requestContext.abortWith(response);
    }
}
