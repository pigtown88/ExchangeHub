package com.service;

import com.dao.member.UserDao;
import com.dao.member.TokenDao;
import com.model.member.LoginRequest;
import com.model.member.TokenResponse;
import com.model.member.User;
import com.model.member.UserToken;
import com.utils.jwt.JwtUtil;
import com.utils.keys.PemKeyManager;
import com.utils.password.PasswordEncoder;

import io.jsonwebtoken.Claims;

import com.service.response.ServiceResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.UUID;

/**
 * 認證服務類
 * 負責處理用戶認證相關的所有業務邏輯
 */
public class AuthenticationService {
    private static final Logger logger = LogManager.getLogger(AuthenticationService.class);
    
    private final UserDao userDao;
    private final TokenDao tokenDao;
    private final PemKeyManager keyManager;
    private final PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;  // 動態初始化
    
    public AuthenticationService() {
        this.userDao = new UserDao();
        this.tokenDao = new TokenDao();
        this.keyManager = PemKeyManager.getInstance();
        this.passwordEncoder = PasswordEncoder.getInstance();
    }
    
    /**
     * 處理用戶登入
     */
    public ServiceResult<TokenResponse> login(LoginRequest request) {
        logger.info("處理用戶登入請求: {}", request.getUsername());
        
        // 參數驗證
        ServiceResult<Void> validationResult = validateLoginRequest(request);
        if (!validationResult.isSuccess()) {
            return ServiceResult.failure(validationResult.getCode(), validationResult.getMessage());
        }

        try {
            // 查找並驗證用戶
            User user = findAndValidateUser(request);
            if (user == null) {
                return ServiceResult.failure("S_101", "用戶名或密碼不正確");
            }

            // 生成或獲取密鑰ID
            String keyId = getOrCreateKeyId(user);
            
            // 生成Token
            jwtUtil = new JwtUtil(user.getId(), keyId);
            UserToken userToken = jwtUtil.generateToken(user, request.getServerName());
            tokenDao.create(userToken);

            logger.info("用戶登入成功: {}", request.getUsername());
            return ServiceResult.success(
                new TokenResponse(userToken.getToken(), userToken.getExpiryDate()),
                "登入成功"
            );
        } catch (Exception e) {
            logger.error("登入過程發生錯誤", e);
            return ServiceResult.failure("S_500", "系統處理過程發生錯誤");
        }
    }
    
    /**
     * 查找並驗證用戶
     */
    private User findAndValidateUser(LoginRequest request) {
        User user = userDao.findByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return null;
        }
        return user;
    }

    /**
     * 獲取或創建密鑰ID
     */
    private String getOrCreateKeyId(User user) {
        String keyId = user.getKeyId();
        
        // 如果用戶沒有密鑰或密鑰不存在，則創建新的
        if (keyId == null || !keyManager.areUserKeysExist(user.getId(), keyId)) {
            keyId = UUID.randomUUID().toString();
            keyManager.generateUserKeyPair(user.getId(), keyId);
            
            // 更新用戶的密鑰ID
            user.setKeyId(keyId);
            userDao.update(user);
            
            logger.info("為用戶 {} 創建新的密鑰對: {}", user.getId(), keyId);
        }
        
        return keyId;
    }

    /**
     * 處理用戶登出
     */
    public ServiceResult<Void> logout(String token) {
        logger.info("處理用戶登出請求");

        if (token == null || token.trim().isEmpty()) {
            return ServiceResult.failure("S_201", "Token不能為空");
        }

        try {
            initializeJwtUtil(token);
            if (!jwtUtil.validateToken(token)) {
                return ServiceResult.failure("S_202", "Token無效");
            }

            String tokenId = jwtUtil.extractTokenId(token);
            tokenDao.delete(tokenId);

            logger.info("用戶登出成功");
            return ServiceResult.success(null, "登出成功");
        } catch (Exception e) {
            logger.error("登出過程發生錯誤", e);
            return ServiceResult.failure("S_500", "系統處理過程發生錯誤");
        }
    }

    /**
     * 驗證Token
     */
    public ServiceResult<Boolean> validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return ServiceResult.failure("S_201", "Token不能為空");
        }

        try {
            initializeJwtUtil(token);
            boolean isValid = jwtUtil.validateToken(token) && 
                            tokenDao.findByTokenId(jwtUtil.extractTokenId(token)) != null;

            return ServiceResult.success(isValid, isValid ? "Token有效" : "Token無效");
        } catch (Exception e) {
            logger.error("Token驗證過程發生錯誤", e);
            return ServiceResult.failure("S_500", "系統處理過程發生錯誤");
        }
    }

    /**
     * 初始化JwtUtil
     * @throws Exception 
     */
    private ServiceResult <Void> initializeJwtUtil(String token){
        try {
            // 不驗證簽名，直接解析 token 內容
            Claims claims = JwtUtil.getUnverifiedClaims(token);
            
            // 從 claims 中獲取必要信息
            Long userId = claims.get("userId", Long.class);
            String keyId = claims.get("keyId", String.class);
            
            if (userId == null || keyId == null) {
//                throw new Exception("Token 格式無效");
                return ServiceResult.failure("S_003", "Token 格式無效");
            }

            // 初始化 JwtUtil，設置正確的用戶密鑰信息
            this.jwtUtil = new JwtUtil(userId, keyId);
            return ServiceResult.success();   //回頭看一下為什麼不能這樣寫

        } catch (Exception e) {
            logger.error("初始化 JwtUtil 失敗", e);
//            throw new Exception("Token 無效: " + e.getMessage());
            return ServiceResult.failure("S_004", "Token 無效");   //回頭看一下為什麼不能這樣寫
        }
    }


    /**
     * 驗證登入請求參數
     */
    private ServiceResult<Void> validateLoginRequest(LoginRequest request) {
        if (request == null) {
            return ServiceResult.failure("S_001", "請求對象不能為空");
        }
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return ServiceResult.failure("S_002", "用戶名不能為空");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return ServiceResult.failure("S_003", "密碼不能為空");
        }
        if (request.getServerName() == null || request.getServerName().trim().isEmpty()) {
            return ServiceResult.failure("S_004", "服務器名稱不能為空");
        }
        return ServiceResult.success();
    }
}