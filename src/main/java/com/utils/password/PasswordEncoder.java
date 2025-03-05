package com.utils.password;


import org.mindrot.jbcrypt.BCrypt;

import com.exception.ErrorCodes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;





import com.exception.UtilException;


/**
 * 密碼加密工具類
 */
public class PasswordEncoder {
    private static final Logger logger = LogManager.getLogger(PasswordEncoder.class);
    private static final int WORK_FACTOR = 12;
    private static volatile PasswordEncoder instance;

    public static PasswordEncoder getInstance() {
        if (instance == null) {
            synchronized (PasswordEncoder.class) {
                if (instance == null) {
                    instance = new PasswordEncoder();
                }
            }
        }
        return instance;
    }

    private PasswordEncoder() {} // 私有構造函數

    /**
     * 加密密碼
     * @param rawPassword 原始密碼
     * @return 加密後的密碼
     * @throws UtilException 當加密過程發生錯誤時
     */
    public String encode(String rawPassword) {
        try {
            // 參數驗證
            if (rawPassword == null || rawPassword.trim().isEmpty()) {
                logger.error("嘗試加密空密碼");
                throw new UtilException(
                    ErrorCodes.PASSWORD_EMPTY,
                    "密碼不能為空"
                );
            }

            // 檢查密碼長度
            if (rawPassword.length() < 6 || rawPassword.length() > 32) {
                logger.error("密碼長度不符合要求");
                throw new UtilException(
                    ErrorCodes.PASSWORD_LENGTH_INVALID,
                    "密碼長度必須在6-32個字符之間"
                );
            }

            String salt = BCrypt.gensalt(WORK_FACTOR);
            return BCrypt.hashpw(rawPassword, salt);

        } catch (IllegalArgumentException e) {
            logger.error("密碼格式無效", e);
            throw new UtilException(
                ErrorCodes.PASSWORD_FORMAT_INVALID,
                "密碼格式無效"
            );
        } catch (Exception e) {
            logger.error("密碼加密過程發生錯誤", e);
            throw new UtilException(
                ErrorCodes.PASSWORD_ENCRYPTION_ERROR,
                "密碼加密過程發生錯誤"
            );
        }
    }

    /**
     * 驗證密碼
     * @param rawPassword 原始密碼
     * @param encodedPassword 加密後的密碼
     * @return 是否匹配
     * @throws UtilException 當驗證過程發生錯誤時
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        try {
            // 參數驗證
            if (rawPassword == null || rawPassword.trim().isEmpty()) {
                logger.error("原始密碼為空");
                throw new UtilException(
                    ErrorCodes.PASSWORD_EMPTY,
                    "原始密碼不能為空"
                );
            }

            if (encodedPassword == null || encodedPassword.trim().isEmpty()) {
                logger.error("加密密碼為空");
                throw new UtilException(
                    ErrorCodes.PASSWORD_HASH_EMPTY,
                    "加密密碼不能為空"
                );
            }

            return BCrypt.checkpw(rawPassword, encodedPassword);

        } catch (IllegalArgumentException e) {
            logger.error("密碼格式無效", e);
            throw new UtilException(
                ErrorCodes.PASSWORD_FORMAT_INVALID,
                "密碼格式無效"
            );
        } catch (UtilException e) {
            throw e;
        } catch (Exception e) {
            logger.error("密碼驗證過程發生錯誤", e);
            throw new UtilException(
                ErrorCodes.PASSWORD_VALIDATION_ERROR,
                "密碼驗證過程發生錯誤"
            );
        }
    }
}
//
///**
// * 密碼加密工具類
// */
//public class PasswordEncoder {
//    private static final Logger logger = LogManager.getLogger(PasswordEncoder.class);
//    private static final int WORK_FACTOR = 12;
//    private static volatile PasswordEncoder instance;
//
//    public PasswordEncoder() {} //自己新增的
//
//    public static PasswordEncoder getInstance() {
//        if (instance == null) {
//            synchronized (PasswordEncoder.class) {
//                if (instance == null) {
//                    instance = new PasswordEncoder();
//                }
//            }
//        }
//        return instance;
//    }
//
//    /**
//     * 加密密碼
//     */
//    public String encode(String rawPassword) {
//        try {
//            String salt = BCrypt.gensalt(WORK_FACTOR);
//            return BCrypt.hashpw(rawPassword, salt);
//        } catch (Exception e) {
//            logger.error("密碼加密失敗", e);
//            throw new AuthenticationException(
//                "AT301", 
//                "ENCRYPTION_ERROR", 
//                "密碼加密過程發生錯誤"
//            );
//        }
//    }
//
//    /**
//     * 驗證密碼
//     */
//    public boolean matches(String rawPassword, String encodedPassword) {
//        try {
//            return BCrypt.checkpw(rawPassword, encodedPassword);
//        } catch (Exception e) {
//            logger.error("密碼驗證失敗", e);
//            throw new AuthenticationException(
//                "AT302", 
//                "VALIDATION_ERROR", 
//                "密碼驗證過程發生錯誤"
//            );
//        }
//    }
//}