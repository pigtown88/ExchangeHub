package com.service.test;


import java.time.LocalDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.model.member.LoginRequest;
import com.model.member.TokenResponse;
import com.model.member.User;
import com.service.AuthenticationService;
import com.service.response.ServiceResult;
import com.dao.member.UserDao;

/**
 * AuthenticationService 使用 Result Pattern 的測試類
 */
public class AuthServiceResultTest {
    private static final Logger logger = LogManager.getLogger(AuthServiceResultTest.class);
    
    private static AuthenticationService authService;
    private static UserDao userDao;
    private static User testUser;
    private static String testToken;

    public static void main(String[] args) {
        System.out.println("===== 開始測試 AuthenticationService =====");
        
        setup();
        
        // 1. 測試登入流程
        testLoginProcess();
        
        // 2. 測試 Token 驗證流程
        testTokenValidation();
        
        // 3. 測試登出流程
        testLogoutProcess();
        
        cleanup();
        
        System.out.println("\n===== 測試完成 =====");
    }

    /**
     * 初始化測試環境
     */
    private static void setup() {
        System.out.println("\n----- 初始化測試環境 -----");
        try {
            authService = new AuthenticationService();
            userDao = new UserDao();
            
            // 創建測試用戶
            testUser = new User();
            testUser.setUsername("testUser" + System.currentTimeMillis());
            testUser.setPassword("$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LcdYShH3n8Q.5/mNm"); // "password123" 的 BCrypt hash
            testUser = userDao.create(testUser);
            System.out.println("測試用戶創建成功：" + testUser.getUsername());
            
        } catch (Exception e) {
            System.err.println("設置測試環境失敗：" + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * 測試登入流程
     */
    private static void testLoginProcess() {
        System.out.println("\n===== 測試登入流程 =====");
        
        // 1. 測試正常登入
        System.out.println("\n----- 測試正常登入 -----");
        LoginRequest validRequest = createLoginRequest(testUser.getUsername(), "password123", "TestServer");
        ServiceResult<TokenResponse> loginResult = authService.login(validRequest);
        printResult("正常登入", loginResult);
        
        if (loginResult.isSuccess()) {
            testToken = loginResult.getData().getToken();
        }
        
        // 2. 測試參數驗證
        System.out.println("\n----- 測試參數驗證 -----");
        
        // 空用戶名
        LoginRequest nullUsernameRequest = createLoginRequest(null, "password123", "TestServer");
        printResult("空用戶名", authService.login(nullUsernameRequest));
        
        // 空密碼
        LoginRequest nullPasswordRequest = createLoginRequest(testUser.getUsername(), null, "TestServer");
        printResult("空密碼", authService.login(nullPasswordRequest));
        
        // 空服務器名
        LoginRequest nullServerRequest = createLoginRequest(testUser.getUsername(), "password123", null);
        printResult("空服務器名", authService.login(nullServerRequest));
        
        // 3. 測試錯誤密碼
        System.out.println("\n----- 測試錯誤密碼 -----");
        LoginRequest wrongPasswordRequest = createLoginRequest(testUser.getUsername(), "wrongpassword", "TestServer");
        printResult("錯誤密碼", authService.login(wrongPasswordRequest));
        
        // 4. 測試不存在的用戶
        System.out.println("\n----- 測試不存在的用戶 -----");
        LoginRequest nonExistentRequest = createLoginRequest("nonexistent", "password123", "TestServer");
        printResult("不存在的用戶", authService.login(nonExistentRequest));
    }

    /**
     * 測試 Token 驗證流程
     */
    private static void testTokenValidation() {
        System.out.println("\n===== 測試 Token 驗證流程 =====");
        
        if (testToken != null) {
            // 1. 測試有效 Token
            System.out.println("\n----- 測試有效 Token -----");
            ServiceResult<Boolean> validResult = authService.validateToken(testToken);
            printResult("有效 Token 驗證", validResult);
        }
        
        // 2. 測試無效 Token
        System.out.println("\n----- 測試無效 Token -----");
        ServiceResult<Boolean> invalidResult = authService.validateToken("invalid.token.here");
        printResult("無效 Token 驗證", invalidResult);
        
        // 3. 測試空 Token
        System.out.println("\n----- 測試空 Token -----");
        ServiceResult<Boolean> nullResult = authService.validateToken(null);
        printResult("空 Token 驗證", nullResult);
    }

    /**
     * 測試登出流程
     */
    private static void testLogoutProcess() {
        System.out.println("\n===== 測試登出流程 =====");
        
        if (testToken != null) {
            // 1. 測試正常登出
            System.out.println("\n----- 測試正常登出 -----");
            ServiceResult<Void> logoutResult = authService.logout(testToken);
            printResult("正常登出", logoutResult);
            
            // 2. 測試重複登出
            System.out.println("\n----- 測試重複登出 -----");
            ServiceResult<Void> duplicateLogoutResult = authService.logout(testToken);
            printResult("重複登出", duplicateLogoutResult);
        }
        
        // 3. 測試無效 Token 登出
        System.out.println("\n----- 測試無效 Token 登出 -----");
        ServiceResult<Void> invalidLogoutResult = authService.logout("invalid.token.here");
        printResult("無效 Token 登出", invalidLogoutResult);
        
        // 4. 測試空 Token 登出
        System.out.println("\n----- 測試空 Token 登出 -----");
        ServiceResult<Void> nullLogoutResult = authService.logout(null);
        printResult("空 Token 登出", nullLogoutResult);
    }

    /**
     * 創建登入請求
     */
    private static LoginRequest createLoginRequest(String username, String password, String serverName) {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setServerName(serverName);
        return request;
    }

    /**
     * 打印測試結果
     */
    private static <T> void printResult(String testCase, ServiceResult<T> result) {
        System.out.println("測試案例: " + testCase);
        System.out.println("結果狀態: " + (result.isSuccess() ? "成功" : "失敗"));
        System.out.println("錯誤代碼: " + result.getCode());
        System.out.println("錯誤信息: " + result.getMessage());
        if (result.getData() != null) {
            System.out.println("返回數據: " + result.getData());
        }
        System.out.println("時間戳: " + result.getTimestamp());
        System.out.println();
    }

    /**
     * 清理測試環境
     */
    private static void cleanup() {
        System.out.println("\n----- 清理測試環境 -----");
        try {
            if (testUser != null && testUser.getId() != null) {
                userDao.delete(testUser.getId());
                System.out.println("測試用戶刪除成功");
            }
        } catch (Exception e) {
            System.err.println("清理測試環境失敗：" + e.getMessage());
        }
    }
}