package com.test.jwtTest;

import java.time.LocalDateTime;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.controller.AuthController;
import com.dao.member.UserDao;
import com.model.member.LoginRequest;
import com.model.member.TokenResponse;
import com.model.member.User;
import com.utils.password.PasswordEncoder;
//測試一下整個登入到登出的TOKEN KEY是否都正常運作
//登出後tokne會自動失效的小測試
public class AuthSystemAllTest {
    private static final Logger logger = LogManager.getLogger(AuthSystemAllTest.class);

    public static void main(String[] args) {
        // 初始化所需組件
        AuthController authController = new AuthController();
        UserDao userDao = new UserDao();
        PasswordEncoder passwordEncoder = PasswordEncoder.getInstance();
 
        // 定義測試參數
        String username = "testUser12387890";//123
        String password = "password123";
        String serverName = "TestServer";

        try {
            logger.info("開始執行認證系統測試流程");
            logger.info("=========================");

            // 第一步：準備測試用戶
            prepareTestUser(userDao, passwordEncoder, username, password);

            // 第二步：測試登入流程
            logger.info("測試登入流程：");
            LoginRequest loginRequest = new LoginRequest(username, password, serverName);
            Response loginResponse = authController.login(loginRequest);

            if (loginResponse.getStatus() != Response.Status.OK.getStatusCode()) {
                logger.error("登入失敗: {}", loginResponse.getStatus());
                return;
            }

            TokenResponse tokenResponse = (TokenResponse) loginResponse.getEntity();
            String token = tokenResponse.getToken();
            logger.info("登入成功，獲取到Token");

            // 第三步：測試Token驗證
            logger.info("測試Token驗證：");
            Response validateResponse = authController.validateToken("Bearer " + token);
            logger.info("Token驗證結果: {}", 
                (validateResponse.getStatus() == Response.Status.OK.getStatusCode() ? "有效" : "無效"));

            // 第四步：測試登出
            logger.info("測試登出流程：");
            Response logoutResponse = authController.logout("Bearer " + token);
            
            if (logoutResponse.getStatus() == Response.Status.OK.getStatusCode()) {
                logger.info("登出成功");
            } else {
                logger.error("登出失敗: {}", logoutResponse.getStatus());
            }

            // 第五步：驗證登出後的Token
            logger.info("驗證登出後的Token：");
            Response finalValidation = authController.validateToken("Bearer " + token);
            logger.info("登出後Token狀態: {}", 
                (finalValidation.getStatus() == Response.Status.OK.getStatusCode() ? "仍然有效" : "已失效"));

        } catch (Exception e) {
            logger.error("測試過程中發生錯誤：", e);
            logger.error("錯誤類型: {}", e.getClass().getName());
            logger.error("錯誤訊息: {}", e.getMessage());
        }
    }

    private static void prepareTestUser(UserDao userDao, 
                                      PasswordEncoder passwordEncoder,
                                      String username, 
                                      String password) {
        try {
            User existingUser = userDao.findByUsername(username);
            
            if (existingUser == null) {
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setPassword(passwordEncoder.encode(password));
                newUser.setCreatedAt(LocalDateTime.now());
                newUser.setUpdatedAt(LocalDateTime.now());
                
                userDao.create(newUser);
                logger.info("測試用戶創建成功");
            } else {
                existingUser.setPassword(passwordEncoder.encode(password));
                existingUser.setUpdatedAt(LocalDateTime.now());
                userDao.update(existingUser);
                logger.info("測試用戶密碼已更新");
            }
        } catch (Exception e) {
            logger.error("準備測試用戶時發生錯誤：{}", e.getMessage());
            throw e;
        }
    }
}