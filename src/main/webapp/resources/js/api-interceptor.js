console.log('api-interceptor.js剛開始');

const API_CONFIG = { 
    BASE_URL: '/ExchangeHub/api3', 
    TOKEN_KEY: 'auth_token' 
};

$(document).ready(function() {
    // 全局 AJAX 設置
    $.ajaxSetup({
        // 每次 AJAX 請求前執行
        beforeSend: function(xhr, settings) {

            
            // 只攔截特定 API 路徑
            if (settings.url.includes('/api3/')) {
                // 從 sessionStorage 獲取 token
                const token = sessionStorage.getItem(API_CONFIG.TOKEN_KEY);
                
                console.log('Token:', token ? '已找到' : '未找到');
                
                // 如果 token 存在，加入請求頭
                if (token) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + token);
                } else {
                    console.warn('未找到有效的認證 token');
                    
                    // 對於非登入請求，如果沒有 token 則跳轉登入
                    if (!settings.url.includes('/auth/login')) {
                        alert('尚未登入，請重新登入');
                        window.location.href = 'login.html';
                    }
                }
            }
            
            console.groupEnd();
        },
        
        // 請求完成後執行
        complete: function(xhr, status) {
            console.group('API Response Interceptor');
            console.log('Response Status:', status);
            
            // 處理未授權的情況（401）
            if (xhr.status === 401) {
                console.warn('API 請求未授權');
                
                // 清除過期的 token
                sessionStorage.removeItem(API_CONFIG.TOKEN_KEY);
                
                // 非登入頁面跳轉
                if (!window.location.pathname.endsWith('login.html')) {
                    alert('登入狀態已過期，請重新登入');
                    window.location.href = 'login.html';
                }
            }
            
            console.groupEnd();
        }
    });

    // 可選：添加全局 AJAX 錯誤處理
    $(document).ajaxError(function(event, xhr, settings, error) {
        console.group('Global AJAX Error');
        console.error('Request URL:', settings.url);
        console.error('Error Status:', xhr.status);
        console.error('Error:', error);
        console.groupEnd();
    });
});