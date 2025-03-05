package com.utils.keys;

import java.io.File;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PemKeyManagerTest {
    private static final Logger logger = LogManager.getLogger(PemKeyManagerTest.class);
    
    // 測試用常量
    private static final Long TEST_USER_ID = 12345L;
    private static final String TEST_KEY_ID = "test-key-001";

    public static void main(String[] args) {
        testKeyManager();
    }

    public static void testKeyManager() {
        logger.info("開始測試密鑰管理系統");

        try {
            // 測試一：單例模式測試
            testSingleton();

            // 測試二：密鑰生成和存儲測試
            testKeyGeneration();

            // 測試三：密鑰加載測試
            testKeyLoading();

            // 測試四：密鑰功能測試
            testKeyFunctionality();

            // 測試五：密鑰備份和更新測試
            testKeyBackupAndUpdate();

            logger.info("所有測試完成");

        } catch (Exception e) {
            logger.error("測試過程中發生錯誤", e);
        } finally {
            // 清理測試產生的文件
//            cleanupTestFiles();
        }
    }

    private static void testSingleton() {
        logger.info("測試單例模式...");
        PemKeyManager instance1 = PemKeyManager.getInstance();
        PemKeyManager instance2 = PemKeyManager.getInstance();
        
        if (instance1 == instance2) {
            logger.info("單例模式測試通過：兩個實例相同");
        } else {
            logger.error("單例模式測試失敗：產生了不同的實例");
        }
    }

    private static void testKeyGeneration() {
        logger.info("測試密鑰生成...");
        try {
            PemKeyManager keyManager = PemKeyManager.getInstance();
            
            // 生成新的密鑰對
            KeyPair keyPair = keyManager.generateUserKeyPair(TEST_USER_ID, TEST_KEY_ID);
            
            if (keyPair != null && keyManager.areUserKeysExist(TEST_USER_ID, TEST_KEY_ID)) {
                logger.info("密鑰生成測試通過：成功生成並存儲密鑰對");
            } else {
                logger.error("密鑰生成測試失敗：密鑰生成或存儲失敗");
            }
        } catch (Exception e) {
            logger.error("密鑰生成測試失敗", e);
        }
    }

    private static void testKeyLoading() {
        logger.info("測試密鑰加載...");
        try {
            PemKeyManager keyManager = PemKeyManager.getInstance();
            
            PublicKey publicKey = keyManager.loadUserPublicKey(TEST_USER_ID, TEST_KEY_ID);
            PrivateKey privateKey = keyManager.loadUserPrivateKey(TEST_USER_ID, TEST_KEY_ID);
            
            if (publicKey != null && privateKey != null) {
                logger.info("密鑰加載測試通過：成功加載公鑰和私鑰");
            } else {
                logger.error("密鑰加載測試失敗：無法加載密鑰");
            }
        } catch (Exception e) {
            logger.error("密鑰加載測試失敗", e);
        }
    }

    private static void testKeyFunctionality() {
        logger.info("測試密鑰功能...");
        try {
            PemKeyManager keyManager = PemKeyManager.getInstance();
            String testData = "Hello, World!";
            
            // 使用公鑰加密
            PublicKey publicKey = keyManager.loadUserPublicKey(TEST_USER_ID, TEST_KEY_ID);
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedData = encryptCipher.doFinal(testData.getBytes());
            
            // 使用私鑰解密
            PrivateKey privateKey = keyManager.loadUserPrivateKey(TEST_USER_ID, TEST_KEY_ID);
            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedData = decryptCipher.doFinal(encryptedData);
            
            String decryptedText = new String(decryptedData);
            
            if (testData.equals(decryptedText)) {
                logger.info("密鑰功能測試通過：加密解密成功");
            } else {
                logger.error("密鑰功能測試失敗：加密解密結果不匹配");
            }
        } catch (Exception e) {
            logger.error("密鑰功能測試失敗", e);
        }
    }

    private static void testKeyBackupAndUpdate() {
        logger.info("測試密鑰備份和更新...");
        try {
            PemKeyManager keyManager = PemKeyManager.getInstance();
            
            // 測試備份
            keyManager.backupUserKeys(TEST_USER_ID, TEST_KEY_ID);
            logger.info("密鑰備份完成");
            
            // 測試更新
            KeyPair newKeyPair = keyManager.updateUserKeys(TEST_USER_ID, TEST_KEY_ID);
            
            if (newKeyPair != null && keyManager.validateUserKeyPair(TEST_USER_ID, TEST_KEY_ID)) {
                logger.info("密鑰更新測試通過：成功更新並驗證新密鑰對");
            } else {
                logger.error("密鑰更新測試失敗");
            }
        } catch (Exception e) {
            logger.error("密鑰備份和更新測試失敗", e);
        }
    }

    private static void cleanupTestFiles() {
        try {
            PemKeyManager keyManager = PemKeyManager.getInstance();
            keyManager.deleteUserKeys(TEST_USER_ID, TEST_KEY_ID);
            logger.info("測試文件清理完成");
        } catch (Exception e) {
            logger.error("清理測試文件失敗", e);
        }
    }
}