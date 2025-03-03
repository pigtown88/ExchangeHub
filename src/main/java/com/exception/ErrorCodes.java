package com.exception;


//錯誤代碼常量
public class ErrorCodes {
// DAO層錯誤碼 (D_XXX)
public static final String DB_DRIVER_ERROR = "001";     // 驅動加載錯誤
public static final String DB_CONNECTION_ERROR = "002"; // 連接錯誤
public static final String DB_QUERY_ERROR = "003";      // 查詢錯誤
public static final String DB_INSERT_ERROR = "004";     // 插入錯誤
public static final String DB_UPDATE_ERROR = "005";     // 更新錯誤
public static final String DB_DELETE_ERROR = "006";     // 刪除錯誤
public static final String DB_MAPPING_ERROR = "007";    // 數據映射錯誤
public static final String DB_INVALID_PARAM = "008";    // 參數驗證錯誤

// 密碼相關錯誤碼 (U_XXX)
public static final String PASSWORD_EMPTY = "001";           // 密碼為空
public static final String PASSWORD_HASH_EMPTY = "002";     // 加密密碼為空
public static final String PASSWORD_LENGTH_INVALID = "003"; // 密碼長度無效
public static final String PASSWORD_FORMAT_INVALID = "004"; // 密碼格式無效
public static final String PASSWORD_ENCRYPTION_ERROR = "005"; // 加密錯誤
public static final String PASSWORD_VALIDATION_ERROR = "006"; // 驗證錯誤

//JWT相關錯誤碼 (U_XXX)  (加上編號U在 utilexception裡面)
public static final String JWT_GENERATION_ERROR = "101";        // Token生成錯誤
public static final String JWT_VALIDATION_ERROR = "102";        // Token驗證錯誤
public static final String JWT_PARSING_ERROR = "103";          // Token解析錯誤
public static final String JWT_TOKEN_EMPTY = "104";            // Token為空
public static final String JWT_TOKEN_INVALID_FORMAT = "105";   // Token格式無效
public static final String JWT_SIGNATURE_INVALID = "106";      // Token簽名無效
public static final String JWT_PRIVATE_KEY_MISSING = "107";    // 私鑰缺失
public static final String JWT_PUBLIC_KEY_MISSING = "108";     // 公鑰缺失
public static final String JWT_USER_NULL = "109";             // 用戶對象為空
public static final String JWT_USERID_NULL = "110";           // 用戶ID為空
public static final String JWT_USERNAME_EMPTY = "111";        // 用戶名為空
public static final String JWT_SERVER_NAME_EMPTY = "112";     // 服務器名稱為空
public static final String JWT_USERID_EXTRACTION_ERROR = "113"; // 用戶ID提取錯誤
public static final String JWT_TOKENID_EXTRACTION_ERROR = "114"; // Token ID提取錯誤
public static final String JWT_INVALID_EXPIRY = "115";     // 無效的過期時間
public static final String JWT_KEYID_EXTRACTION_ERROR = "116";     // 無效的過期時間
public static final String JWT_TOKEN_EXPIRED = "117";     // 無效的過期時間
public static final String JWT_TOKENID_MISSING = "118";     // 無效的過期時間



// Filter層錯誤碼 (FT_XXX)
public static final String AUTH_HEADER_MISSING = "FT001";  // 缺少認證頭
public static final String AUTH_FORMAT_INVALID = "FT002";  // 認證頭格式錯誤
public static final String TOKEN_EMPTY = "FT003";         // Token為空
public static final String TOKEN_EXPIRED = "FT004";       // Token過期
public static final String TOKEN_INVALID = "FT005";       // Token無效
public static final String TOKEN_VALIDATE_ERROR = "FT006"; // Token驗證錯誤


//金鑰key對相關錯誤
public static final String KEY_ALGORITHM_NOT_FOUND = "KEY001";
public static final String KEY_GENERATION_ERROR = "KEY002";
public static final String KEY_USERID_NULL = "KEY003";
public static final String KEY_ID_EMPTY = "KEY004";
public static final String KEY_DELETION_ERROR = "KEY005";
public static final String KEY_UPDATE_ERROR = "KEY006";
public static final String KEY_LOADING_ERROR = "KEY007";
public static final String KEY_BACKUP_ERROR = "KEY008";
}