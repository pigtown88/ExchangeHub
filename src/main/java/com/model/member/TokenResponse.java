package com.model.member;



import java.time.LocalDateTime;

/**
 * Token響應的資料傳輸物件
 */
public class TokenResponse {
    private String token;
    private LocalDateTime expiryDate;

    // 建構子
    public TokenResponse() {
    }

    public TokenResponse(String token, LocalDateTime expiryDate) {
        this.token = token;
        this.expiryDate = expiryDate;
    }

    // Getter 與 Setter
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}