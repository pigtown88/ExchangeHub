package com.utils.keys;


import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class PemKeyManagerTest2 {
    public static void main(String[] args) {
        try {
            // 初始化密鑰管理器
            PemKeyManager keyManager = PemKeyManager.getInstance();
            System.out.println("密鑰管理器初始化成功");

            // 測試用戶資訊
            Long userId = 12345L;
            String keyId = "test-key-001";

            // 1. 生成新的密鑰對
            System.out.println("\n1. 測試生成密鑰對");
            KeyPair keyPair = keyManager.generateUserKeyPair(userId, keyId);
            System.out.println("成功生成密鑰對");
            System.out.println("公鑰算法: " + keyPair.getPublic().getAlgorithm());
            System.out.println("私鑰算法: " + keyPair.getPrivate().getAlgorithm());

            // 2. 檢查密鑰是否存在
            System.out.println("\n2. 檢查密鑰存在性");
            boolean exists = keyManager.areUserKeysExist(userId, keyId);
            System.out.println("密鑰是否存在: " + exists);

            // 3. 載入並驗證密鑰
            System.out.println("\n3. 載入並驗證密鑰");
            PublicKey publicKey = keyManager.loadUserPublicKey(userId, keyId);
            PrivateKey privateKey = keyManager.loadUserPrivateKey(userId, keyId);
            System.out.println("成功載入密鑰");

            boolean isValid = keyManager.validateUserKeyPair(userId, keyId);
            System.out.println("密鑰對是否有效: " + isValid);

            // 4. 備份密鑰
            System.out.println("\n4. 備份密鑰");
            keyManager.backupUserKeys(userId, keyId);
            System.out.println("密鑰備份完成");

            // 5. 更新密鑰
            System.out.println("\n5. 更新密鑰");
            KeyPair newKeyPair = keyManager.updateUserKeys(userId, keyId);
            System.out.println("密鑰更新完成");
            
            // 再次驗證新密鑰
            boolean isNewValid = keyManager.validateUserKeyPair(userId, keyId);
            System.out.println("新密鑰對是否有效: " + isNewValid);

            // 6. 測試錯誤情況
            System.out.println("\n6. 測試錯誤情況");
            try {
                keyManager.loadUserPublicKey(999L, "non-existent");
            } catch (Exception e) {
                System.out.println("預期的錯誤: " + e.getMessage());
            }

            // 7. 清理測試密鑰
            System.out.println("\n7. 清理測試密鑰");
            keyManager.deleteUserKeys(userId, keyId);
            System.out.println("密鑰清理完成");

            // 最終確認
            boolean stillExists = keyManager.areUserKeysExist(userId, keyId);
            System.out.println("密鑰是否還存在: " + stillExists);

        } catch (Exception e) {
            System.err.println("測試過程中發生錯誤:");
            e.printStackTrace();
        }
    }
}