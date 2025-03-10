package com.test.jwtTest;


import com.dao.member.UserDao;
import com.dao.member.TokenDao;
import com.model.member.User;
import com.model.member.UserToken;
import java.time.LocalDateTime;
//創建用戶 token
public class TestUserToken {
    public static void main(String[] args) {
        try {
            // 創建資料庫訪問對象
            UserDao userRepo = new UserDao();
            TokenDao tokenRepo = new TokenDao();

            // 創建測試用戶
            User testUser = new User();
            testUser.setUsername("testUser123466");
            testUser.setPassword("password123");

            System.out.println("開始創建用戶...");
            User createdUser = userRepo.create(testUser);
            System.out.println("用戶創建成功！ID: " + createdUser.getId());

            // 創建用戶token
            UserToken token = new UserToken();
            token.setUserId(createdUser.getId());
            token.setTokenId("test-token-" + System.currentTimeMillis());
            token.setToken("test-token-value");
            token.setServerName("test-server");
            token.setIssuedAt(LocalDateTime.now());
            token.setExpiryDate(LocalDateTime.now().plusHours(1));

            System.out.println("開始創建Token...");
            UserToken createdToken = tokenRepo.create(token);
            System.out.println("Token創建成功！ID: " + createdToken.getId());

            // 測試查詢功能
            System.out.println("\n測試查詢功能：");
            User foundUser = userRepo.findById(createdUser.getId());
            System.out.println("查找用戶：" + foundUser.getUsername());

            UserToken foundToken = tokenRepo.findByTokenId(token.getTokenId());
            System.out.println("查找Token：" + foundToken.getTokenId());

            // 清理測試數據
//            System.out.println("\n清理測試數據...");
//            tokenRepo.delete(token.getTokenId());
//            userRepo.delete(createdUser.getId());
//            System.out.println("測試數據清理完成！");

        } catch (Exception e) {
            System.err.println("測試過程中發生錯誤：");
            e.printStackTrace();
        }
    }
}




//
//import org.junit.jupiter.api.*;
//import com.model.member.User;
//import com.model.member.UserToken;
//import java.time.LocalDateTime;
//import java.util.List;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class RepositoryTests {
//    private UserRepository userRepository;
//    private TokenRepository tokenRepository;
//    private User testUser;
//    
//    @BeforeEach
//    void setUp() {
//        userRepository = new UserRepository();
//        tokenRepository = new TokenRepository();
//        
//        // 創建測試用戶
//        testUser = new User();
//        testUser.setUsername("testUser" + System.currentTimeMillis());
//        testUser.setPassword("testPassword");
//    }
//    
//    @Test
//    void testUserCRUD() {
//        // 測試創建用戶
//        User createdUser = userRepository.create(testUser);
//        assertNotNull(createdUser.getId(), "用戶ID不應為空");
//        
//        // 測試查詢用戶
//        User foundUser = userRepository.findById(createdUser.getId());
//        assertNotNull(foundUser, "應能找到已創建的用戶");
//        assertEquals(testUser.getUsername(), foundUser.getUsername());
//        
//        // 測試更新用戶
//        String newPassword = "newPassword";
//        foundUser.setPassword(newPassword);
//        userRepository.update(foundUser);
//        User updatedUser = userRepository.findById(foundUser.getId());
//        assertEquals(newPassword, updatedUser.getPassword());
//        
//        // 測試刪除用戶
//        userRepository.delete(foundUser.getId());
//        User deletedUser = userRepository.findById(foundUser.getId());
//        assertNull(deletedUser, "用戶應該已被刪除");
//    }
//    
//    @Test
//    void testTokenCRUD() {
//        // 先創建一個用戶
//        User user = userRepository.create(testUser);
//        
//        // 創建測試Token
//        UserToken token = new UserToken();
//        token.setUserId(user.getId());
//        token.setTokenId("test-token-" + System.currentTimeMillis());
//        token.setToken("actual-token-value");
//        token.setServerName("test-server");
//        token.setIssuedAt(LocalDateTime.now());
//        token.setExpiryDate(LocalDateTime.now().plusHours(1));
//        
//        // 測試創建Token
//        UserToken createdToken = tokenRepository.create(token);
//        assertNotNull(createdToken.getId(), "Token ID不應為空");
//        
//        // 測試查詢Token
//        UserToken foundToken = tokenRepository.findByTokenId(token.getTokenId());
//        assertNotNull(foundToken, "應能找到已創建的Token");
//        assertEquals(token.getToken(), foundToken.getToken());
//        
//        // 測試查詢用戶的所有Token
//        List<UserToken> userTokens = tokenRepository.findByUserId(user.getId());
//        assertFalse(userTokens.isEmpty(), "用戶應該有Token");
//        assertEquals(1, userTokens.size(), "用戶應該只有一個Token");
//        
//        // 測試刪除Token
//        tokenRepository.delete(token.getTokenId());
//        UserToken deletedToken = tokenRepository.findByTokenId(token.getTokenId());
//        assertNull(deletedToken, "Token應該已被刪除");
//        
//        // 清理測試用戶
//        userRepository.delete(user.getId());
//    }
//    
//    @Test
//    void testExpiredTokenDeletion() {
//        // 先創建一個用戶
//        User user = userRepository.create(testUser);
//        
//        // 創建一個已過期的Token
//        UserToken expiredToken = new UserToken();
//        expiredToken.setUserId(user.getId());
//        expiredToken.setTokenId("expired-token-" + System.currentTimeMillis());
//        expiredToken.setToken("expired-token-value");
//        expiredToken.setServerName("test-server");
//        expiredToken.setIssuedAt(LocalDateTime.now().minusDays(2));
//        expiredToken.setExpiryDate(LocalDateTime.now().minusDays(1));
//        
//        tokenRepository.create(expiredToken);
//        
//        // 測試刪除過期Token
//        tokenRepository.deleteExpiredTokens();
//        
//        // 驗證過期Token已被刪除
//        UserToken deletedToken = tokenRepository.findByTokenId(expiredToken.getTokenId());
//        assertNull(deletedToken, "過期Token應該已被刪除");
//        
//        // 清理測試用戶
//        userRepository.delete(user.getId());
//    }
//}