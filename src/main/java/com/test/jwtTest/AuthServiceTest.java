//package com.test.jwtTest;
//
//import java.time.LocalDateTime;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import com.dao.member.UserDao;
//import com.model.member.LoginRequest;
//import com.model.member.TokenResponse;
//import com.model.member.User;
//import com.service.AuthenticationService;
//import com.utils.password.PasswordEncoder;
//
//public class AuthServiceTest {
//    private static final Logger logger = LogManager.getLogger(AuthServiceTest.class);
//
//    public static void main(String[] args) {
//        // 初始化所需組件
//        UserDao userDao = new UserDao();
//        PasswordEncoder passwordEncoder = PasswordEncoder.getInstance();
//        AuthenticationService authService = new AuthenticationService();
//
//        // 測試用戶資訊
//        String username = "testUser";
//        String rawPassword = "password123";
//        String serverName = "TestServer";
//
//        try {
//            // 第一步：檢查並創建測試用戶
//            User existingUser = userDao.findByUsername(username);
//            User testUser;
//
//            if (existingUser == null) {
//                // 創建新用戶
//                testUser = new User();
//                testUser.setUsername(username);
//                testUser.setPassword(passwordEncoder.encode(rawPassword)); // 加密密碼
//                testUser.setCreatedAt(LocalDateTime.now());
//                testUser.setUpdatedAt(LocalDateTime.now());
//                testUser = userDao.create(testUser);
//                logger.info("測試用戶創建成功，ID: {}", testUser.getId());
//            } else {
//                // 更新現有用戶的密碼
//                existingUser.setPassword(passwordEncoder.encode(rawPassword));
//                existingUser.setUpdatedAt(LocalDateTime.now());
//                userDao.update(existingUser);
//                testUser = existingUser;
//                logger.info("測試用戶密碼已更新");
//            }
//
//            // 第二步：測試登入
//            logger.info("\n開始測試登入流程...");
//            LoginRequest loginRequest = new LoginRequest(username, rawPassword, serverName);
//            TokenResponse response = authService.login(loginRequest);
//
//            if (response != null && response.getToken() != null) {
//                logger.info("登入成功！");
//                logger.info("Token: {}", response.getToken());
//                logger.info("過期時間: {}", response.getExpiryDate());
//            }
//        } catch (Exception e) {
//            logger.error("測試過程中發生錯誤：");
//            logger.error("錯誤訊息: {}", e.getMessage(), e);
//        }
//    }
//}