package com.utils.keys;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import com.exception.ErrorCodes;
import com.exception.UtilException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.util.concurrent.locks.ReentrantLock;
import javax.crypto.Cipher;

/**
 * RSA密鑰對管理器
 * 負責管理每個用戶的PEM格式RSA公私鑰對，提供密鑰的生成、存儲和讀取功能
 */
public class PemKeyManager {
    private static final Logger logger = LogManager.getLogger(PemKeyManager.class);

    // 密鑰相關的常量配置
    private static final String KEY_DIR = "config/keys";
    private static final int KEY_SIZE = 2048;

    // 單例實例和鎖
    private static volatile PemKeyManager instance;
    private static final ReentrantLock lock = new ReentrantLock();

    /**
     * 獲取PemKeyManager的單例實例
     */
    public static PemKeyManager getInstance() {
        if (instance == null) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = new PemKeyManager();
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    /**
     * 私有構造函數，初始化密鑰管理器
     */
    private PemKeyManager() {
        logger.info("正在初始化密鑰管理器...");
        initialize();
    }

    /**
     * 初始化密鑰管理系統
     */
    private void initialize() {
        try {
            createKeyDirectoryIfNotExists();
            logger.info("密鑰管理器初始化完成");
        } catch (Exception e) {
            logger.error("密鑰管理器初始化失敗", e);
            throw new RuntimeException("密鑰管理器初始化失敗", e);
        }
    }

    /**
     * 確保密鑰存儲目錄存在
     */
    private void createKeyDirectoryIfNotExists() throws IOException {
        Path directory = Paths.get(KEY_DIR);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
            logger.info("創建密鑰目錄: {}", KEY_DIR);
        }
    }

    /**
     * 生成用戶私鑰路徑
     */
    private String getPrivateKeyPath(Long userId, String keyId) {
        return String.format("%s/%d/private_%s.pem", KEY_DIR, userId, keyId);
    }

    /**
     * 生成用戶公鑰路徑
     */
    private String getPublicKeyPath(Long userId, String keyId) {
        return String.format("%s/%d/public_%s.pem", KEY_DIR, userId, keyId);
    }

    /**
     * 為用戶生成新的RSA密鑰對
     */
    public KeyPair generateUserKeyPair(Long userId, String keyId) {
        logger.debug("開始為用戶 {} 生成新的RSA密鑰對...", userId);
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(KEY_SIZE, new SecureRandom());
            KeyPair keyPair = generator.generateKeyPair();

            storeUserKeys(userId, keyId, keyPair);
            logger.info("成功為用戶 {} 生成並儲存密鑰對", userId);

            return keyPair;
        } catch (NoSuchAlgorithmException e) {
            logger.error("RSA算法不可用", e);
            throw new UtilException(ErrorCodes.KEY_GENERATION_ERROR, "RSA初始化發生錯誤");
        } catch (Exception e) {
            logger.error("生成密鑰對時發生錯誤", e);
            throw new UtilException(ErrorCodes.KEY_GENERATION_ERROR, "密鑰生成過程中發生錯誤");
        }
    }

    /**
     * 存儲用戶的密鑰對
     */
    private void storeUserKeys(Long userId, String keyId, KeyPair keyPair) throws Exception {
        String userKeyDir = String.format("%s/%d", KEY_DIR, userId);
        Files.createDirectories(Paths.get(userKeyDir));

        // 存儲私鑰
        String privatePath = getPrivateKeyPath(userId, keyId);
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(new FileWriter(privatePath))) {
            pemWriter.writeObject(keyPair.getPrivate());
            logger.debug("用戶 {} 的私鑰已存儲到: {}", userId, privatePath);
        }

        // 存儲公鑰
        String publicPath = getPublicKeyPath(userId, keyId);
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(new FileWriter(publicPath))) {
            pemWriter.writeObject(keyPair.getPublic());
            logger.debug("用戶 {} 的公鑰已存儲到: {}", userId, publicPath);
        }
    }

    /**
     * 載入用戶的公鑰
     */
    public PublicKey loadUserPublicKey(Long userId, String keyId) throws Exception {
        String publicPath = getPublicKeyPath(userId, keyId);
        try (PEMParser pemParser = new PEMParser(new FileReader(publicPath))) {
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            Object object = pemParser.readObject();
            return converter.getPublicKey((SubjectPublicKeyInfo) object);
        } catch (Exception e) {
            logger.error("讀取用戶 {} 的公鑰失敗", userId, e);
            throw new UtilException(ErrorCodes.KEY_LOADING_ERROR, "公鑰讀取失敗");
        }
    }

    /**
     * 載入用戶的私鑰
     */
    public PrivateKey loadUserPrivateKey(Long userId, String keyId) throws Exception {
        String privatePath = getPrivateKeyPath(userId, keyId);
        try (PEMParser pemParser = new PEMParser(new FileReader(privatePath))) {
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            Object object = pemParser.readObject();
            
            if (object instanceof PEMKeyPair) {
                return converter.getPrivateKey(((PEMKeyPair) object).getPrivateKeyInfo());
            } else if (object instanceof PrivateKeyInfo) {
                return converter.getPrivateKey((PrivateKeyInfo) object);
            } else {
                throw new IllegalArgumentException("無法識別的私鑰格式");
            }
        } catch (Exception e) {
            logger.error("讀取用戶 {} 的私鑰失敗", userId, e);
            throw new UtilException(ErrorCodes.KEY_LOADING_ERROR, "私鑰讀取失敗");
        }
    }

    /**
     * 驗證用戶的密鑰對
     */
    public boolean validateUserKeyPair(Long userId, String keyId) {
        try {
            PublicKey publicKey = loadUserPublicKey(userId, keyId);
            PrivateKey privateKey = loadUserPrivateKey(userId, keyId);

            Cipher cipher = Cipher.getInstance("RSA");
            byte[] testData = "test".getBytes();

            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encrypted = cipher.doFinal(testData);

            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decrypted = cipher.doFinal(encrypted);

            return java.util.Arrays.equals(testData, decrypted);
        } catch (Exception e) {
            logger.error("用戶 {} 的密鑰對驗證失敗", userId, e);
            return false;
        }
    }

    /**
     * 檢查用戶的密鑰是否存在
     */
    public boolean areUserKeysExist(Long userId, String keyId) {
        try {
            return Files.exists(Paths.get(getPrivateKeyPath(userId, keyId))) &&
                   Files.exists(Paths.get(getPublicKeyPath(userId, keyId)));
        } catch (Exception e) {
            logger.error("檢查用戶 {} 的密鑰存在性時發生錯誤", userId, e);
            return false;
        }
    }

    /**
     * 刪除用戶的密鑰對
     */
    public void deleteUserKeys(Long userId, String keyId) {
        try {
            Files.deleteIfExists(Paths.get(getPrivateKeyPath(userId, keyId)));
            Files.deleteIfExists(Paths.get(getPublicKeyPath(userId, keyId)));
            logger.info("成功刪除用戶 {} 的密鑰對", userId);
        } catch (Exception e) {
            logger.error("刪除用戶 {} 的密鑰對時發生錯誤", userId, e);
            throw new UtilException(ErrorCodes.KEY_DELETION_ERROR, "密鑰刪除失敗");
        }
    }

    /**
     * 更新用戶的密鑰對
     */
    public KeyPair updateUserKeys(Long userId, String keyId) {
        logger.info("開始更新用戶 {} 的密鑰對", userId);
        try {
            // 先備份舊的密鑰
            backupUserKeys(userId, keyId);
            // 刪除舊的密鑰
            deleteUserKeys(userId, keyId);
            // 生成新的密鑰對
            return generateUserKeyPair(userId, keyId);
        } catch (Exception e) {
            logger.error("更新用戶 {} 的密鑰對時發生錯誤", userId, e);
            throw new UtilException(ErrorCodes.KEY_UPDATE_ERROR, "密鑰更新失敗");
        }
    }

    /**
     * 備份用戶的密鑰對
     */
    public void backupUserKeys(Long userId, String keyId) {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String backupPrivatePath = getPrivateKeyPath(userId, keyId) + "." + timestamp + ".backup";
            String backupPublicPath = getPublicKeyPath(userId, keyId) + "." + timestamp + ".backup";

            Files.copy(Paths.get(getPrivateKeyPath(userId, keyId)), Paths.get(backupPrivatePath));
            Files.copy(Paths.get(getPublicKeyPath(userId, keyId)), Paths.get(backupPublicPath));

            logger.info("成功備份用戶 {} 的密鑰對", userId);
        } catch (Exception e) {
            logger.error("備份用戶 {} 的密鑰對時發生錯誤", userId, e);
            throw new UtilException(ErrorCodes.KEY_BACKUP_ERROR, "密鑰備份失敗");
        }
    }
}