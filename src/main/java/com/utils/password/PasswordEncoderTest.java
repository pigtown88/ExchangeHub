package com.utils.password;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.utils.password.PasswordEncoder;
import com.exception.UtilException;

/**
 * PasswordEncoder測試類
 */
public class PasswordEncoderTest {
    private static final Logger logger = LogManager.getLogger(PasswordEncoderTest.class);

    public static void main(String[] args) {
        // 獲取PasswordEncoder實例
        PasswordEncoder encoder = PasswordEncoder.getInstance();
        
        // 1. 測試正常加密和驗證
        testNormalCase(encoder);
        
        // 2. 測試邊界情況
        testBoundaryCase(encoder);
        
        // 3. 測試錯誤情況
        testErrorCase(encoder);
    }

    /**
     * 測試正常情況
     */
    private static void testNormalCase(PasswordEncoder encoder) {
        System.out.println("===== 測試正常情況 =====");
        try {
            // 測試基本密碼加密
            String rawPassword = "Password123";
            System.out.println("原始密碼: " + rawPassword);
            
            String encodedPassword = encoder.encode(rawPassword);
            System.out.println("加密後密碼: " + encodedPassword);
            
            // 測試密碼驗證
            boolean isMatch = encoder.matches(rawPassword, encodedPassword);
            System.out.println("密碼驗證結果: " + isMatch);
            
            // 測試錯誤密碼
            boolean wrongMatch = encoder.matches("WrongPassword", encodedPassword);
            System.out.println("錯誤密碼驗證結果: " + wrongMatch);
            
        } catch (UtilException e) {
            System.err.println("錯誤代碼: " + e.getCode());
            System.err.println("錯誤信息: " + e.getMessage());
        }
    }

    /**
     * 測試邊界情況
     */
    private static void testBoundaryCase(PasswordEncoder encoder) {
        System.out.println("\n===== 測試邊界情況 =====");
        
        // 測試最短密碼（6字符）
        testPassword(encoder, "Pass12", "最短密碼（6字符）");
        
        // 測試最長密碼（32字符）
        testPassword(encoder, "Password123456789012345678901234567", "最長密碼（32字符）");
        
        // 測試特殊字符密碼
        testPassword(encoder, "Pass@#$%^&*()", "特殊字符密碼");
        
        // 測試中文密碼
        testPassword(encoder, "密碼Password123", "中文密碼");
    }

    /**
     * 測試錯誤情況
     */
    private static void testErrorCase(PasswordEncoder encoder) {
        System.out.println("\n===== 測試錯誤情況 =====");
        
        // 測試空密碼
        testErrorPassword(encoder, null, "空密碼（null）");
        testErrorPassword(encoder, "", "空密碼（空字符串）");
        testErrorPassword(encoder, "   ", "空密碼（空白字符）");
        
        // 測試過短密碼
        testErrorPassword(encoder, "Pass1", "過短密碼（5字符）");
        
        // 測試過長密碼
        testErrorPassword(encoder, "Password1234567890123456789012345678901", "過長密碼（33字符）");
        
        // 測試錯誤的加密密碼格式
        try {
            encoder.matches("Password123", "invalid_hash_format");
            System.out.println("預期應拋出異常：無效的加密密碼格式");
        } catch (UtilException e) {
            System.out.println("成功捕獲預期的異常：");
            System.out.println("錯誤代碼: " + e.getCode());
            System.out.println("錯誤信息: " + e.getMessage());
        }
    }

    /**
     * 輔助方法：測試單個密碼
     */
    private static void testPassword(PasswordEncoder encoder, String password, String testCase) {
        try {
            System.out.println("\n測試" + testCase + ":");
            System.out.println("密碼: " + password);
            String encoded = encoder.encode(password);
            System.out.println("加密後: " + encoded);
            boolean isMatch = encoder.matches(password, encoded);
            System.out.println("驗證結果: " + isMatch);
        } catch (UtilException e) {
            System.out.println("發生異常:");
            System.out.println("錯誤代碼: " + e.getCode());
            System.out.println("錯誤信息: " + e.getMessage());
        }
    }

    /**
     * 輔助方法：測試錯誤密碼
     */
    private static void testErrorPassword(PasswordEncoder encoder, String password, String testCase) {
        try {
            System.out.println("\n測試" + testCase + ":");
            System.out.println("密碼: " + password);
            String encoded = encoder.encode(password);
            System.out.println("加密結果: " + encoded);
        } catch (UtilException e) {
            System.out.println("成功捕獲預期的異常：");
            System.out.println("錯誤代碼: " + e.getCode());
            System.out.println("錯誤信息: " + e.getMessage());
        }
    }
}
