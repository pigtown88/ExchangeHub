package com.model.member;
/**
 * 登入請求的數據傳輸對象
 * 用於封裝從客戶端接收的登入請求數據
 */
public class LoginRequest {
    private String username;    // 用戶名
    private String password;    // 密碼
    private String serverName;  // 服務器名稱

    // 無參構造函數，用於 JSON 反序列化
    public LoginRequest() {
    }

    // 帶參數的構造函數，用於創建請求對象
    public LoginRequest(String username, String password, String serverName) {
        this.username = username;
        this.password = password;
        this.serverName = serverName;
    }

    // Getter 和 Setter 方法
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
