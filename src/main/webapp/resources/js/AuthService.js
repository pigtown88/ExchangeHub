/**
 * authService.js
 * 提供認證相關的服務方法
 */
import TokenManager from './TokenManager.js';

export default class AuthService {
    /**
     * 處理登入成功後的 Token 儲存
     * @param {Object} response - 登入回應資料
     */
    static handleLoginSuccess(response) {
        try {
            const { token, expiryDate, userInfo, serverName } = response;
            TokenManager.setToken(
                token,
                new Date(expiryDate),
                userInfo,
                serverName
            );
            return true;
        } catch (error) {
            console.error('處理登入回應時發生錯誤:', error);
            throw error;
        }
    }

    /**
     * 處理登出
     */
    static handleLogout() {
        TokenManager.clearToken();
    }

    /**
     * 檢查登入狀態
     * @returns {boolean} 是否已登入
     */
    static checkAuthStatus() {
        if (!TokenManager.isLoggedIn()) {
            return false;
        }

        if (TokenManager.isTokenExpiringSoon(5)) {
            console.warn('Token 即將過期');
            // 這裡可以添加自動更新 Token 的邏輯
        }
        return true;
    }
}