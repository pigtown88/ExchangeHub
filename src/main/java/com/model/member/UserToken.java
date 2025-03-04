
 package com.model.member;


import java.time.LocalDateTime;

/**
 * 代表 UserToken 的資料模型。
 */
public class UserToken {
    private long id;
    private long userId;
    private String tokenId;
    private String token;
    private String serverName;
    private LocalDateTime issuedAt;
    private LocalDateTime expiryDate;

    // 建構子
    public UserToken(long id, long userId, String tokenId, String token, String serverName,
                     LocalDateTime issuedAt, LocalDateTime expiryDate) {
        this.id = id;
        this.userId = userId;
        this.tokenId = tokenId;
        this.token = token;
        this.serverName = serverName;
        this.issuedAt = issuedAt;
        this.expiryDate = expiryDate;
    }

    public UserToken() {
		// TODO Auto-generated constructor stub
	}



	// Getter 與 Setter
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}

