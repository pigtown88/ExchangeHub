package com.dao.member;



import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dao.member.TokenDao;
import com.dao.member.UserDao;
import com.exception.DAOException;
import com.model.member.User;
import com.model.member.UserToken;

/**
 * DAO層測試類
 */
public class UserDaoTest {
    private static final Logger logger = LogManager.getLogger(UserDaoTest.class);

    public static void main(String[] args) {
        testUserDao();
        System.out.println("\n" + "=".repeat(50) + "\n");
        testTokenDao();
    }

    /**
     * 測試UserDao
     */
    private static void testUserDao() {
        UserDao userDao = new UserDao();
        
        System.out.println("===== 開始測試 UserDao =====");
        
        // 測試創建用戶
        try {
            System.out.println("\n----- 測試創建用戶 -----");
            User newUser = new User();
            newUser.setUsername("testUser" + System.currentTimeMillis());
            newUser.setPassword("password123");
            
            User createdUser = userDao.create(newUser);
            System.out.println("創建用戶成功: ID = " + createdUser.getId());
            
            // 測試查詢用戶
            System.out.println("\n----- 測試查詢用戶 -----");
            User foundUser = userDao.findById(createdUser.getId());
            System.out.println("查詢用戶成功: " + foundUser.getUsername());
            
            // 測試更新用戶
            System.out.println("\n----- 測試更新用戶 -----");
            foundUser.setPassword("newPassword123");
            userDao.update(foundUser);
            System.out.println("更新用戶成功");
            
//            // 測試刪除用戶
//            System.out.println("\n----- 測試刪除用戶 -----");
//            userDao.delete(foundUser.getId());
//            System.out.println("刪除用戶成功");
            
        } catch (DAOException e) {
            System.err.println("錯誤代碼: " + e.getCode());
            System.err.println("錯誤信息: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 測試錯誤情況
        System.out.println("\n----- 測試錯誤情況 -----");
        try {
            // 測試空用戶名
            User invalidUser = new User();
            userDao.create(invalidUser);
        } catch (DAOException e) {
            System.out.println("預期的錯誤被捕獲:");
            System.out.println("錯誤代碼: " + e.getCode());
            System.out.println("錯誤信息: " + e.getMessage());
        }
        
        try {
            // 測試查詢不存在的ID
            userDao.findById(-1L);
        } catch (DAOException e) {
            System.out.println("\n預期的錯誤被捕獲:");
            System.out.println("錯誤代碼: " + e.getCode());
            System.out.println("錯誤信息: " + e.getMessage());
        }
    }

    /**
     * 測試TokenDao
     */
    private static void testTokenDao() {
        TokenDao tokenDao = new TokenDao();
        UserDao userDao = new UserDao();
        
        System.out.println("===== 開始測試 TokenDao =====");
        
        try {
            // 先創建一個測試用戶
            System.out.println("\n----- 創建測試用戶 -----");
            User testUser = new User();
            testUser.setUsername("testUser" + System.currentTimeMillis());
            testUser.setPassword("password123");
            User createdUser = userDao.create(testUser);
            System.out.println("創建用戶成功，ID: " + createdUser.getId());

            // 測試創建和查詢 Token
            System.out.println("\n----- 測試創建 Token -----");
            UserToken newToken = createSampleToken(createdUser.getId());
            
            UserToken createdToken = tokenDao.create(newToken);
            System.out.println("創建 Token 成功: " + createdToken.getId());
            
            // 測試通過 TokenId 查詢
            System.out.println("\n----- 測試查詢 Token -----");
            UserToken foundToken = tokenDao.findByTokenId(createdToken.getTokenId());
            System.out.println("查詢 Token 成功: " + foundToken.getTokenId());
            
            // 測試查詢用戶的所有Token
            System.out.println("\n----- 測試查詢用戶的所有 Token -----");
            List<UserToken> userTokens = tokenDao.findByUserId(foundToken.getUserId());
            System.out.println("用戶Token數量: " + userTokens.size());
            
//            // 測試刪除 Token
//            System.out.println("\n----- 測試刪除 Token -----");
//            tokenDao.delete(foundToken.getTokenId());
//            System.out.println("刪除 Token 成功");
            
//            // 測試刪除過期Token
//            System.out.println("\n----- 測試刪除過期 Token -----");
//            tokenDao.deleteExpiredTokens();
//            System.out.println("過期Token清理完成");
            
//            // 清理測試數據
//            System.out.println("\n----- 清理測試數據 -----");
//            userDao.delete(createdUser.getId());
//            System.out.println("清理測試用戶成功");
            
        } catch (DAOException e) {
            System.err.println("錯誤代碼: " + e.getCode());
            System.err.println("錯誤信息: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 測試錯誤情況
        System.out.println("\n----- 測試錯誤情況 -----");
        
        // 測試空Token對象
        try {
            tokenDao.create(null);
        } catch (DAOException e) {
            System.out.println("預期的錯誤被捕獲 (空Token對象):");
            System.out.println("錯誤代碼: " + e.getCode());
            System.out.println("錯誤信息: " + e.getMessage());
        }
        
        // 測試無效的TokenId
        try {
            tokenDao.findByTokenId("");
        } catch (DAOException e) {
            System.out.println("\n預期的錯誤被捕獲 (無效TokenId):");
            System.out.println("錯誤代碼: " + e.getCode());
            System.out.println("錯誤信息: " + e.getMessage());
        }
        
        // 測試無效的UserId
        try {
            tokenDao.findByUserId(null);
        } catch (DAOException e) {
            System.out.println("\n預期的錯誤被捕獲 (無效UserId):");
            System.out.println("錯誤代碼: " + e.getCode());
            System.out.println("錯誤信息: " + e.getMessage());
        }
    }
    
    /**
     * 創建測試用的Token對象
     */
    private static UserToken createSampleToken(Long userId) {
        UserToken token = new UserToken();
        token.setUserId(userId);
        token.setTokenId(UUID.randomUUID().toString());
        token.setToken("sample.jwt.token");
        token.setServerName("TestServer");
        token.setIssuedAt(LocalDateTime.now());
        token.setExpiryDate(LocalDateTime.now().plusHours(24));
        return token;
    }
}