$(document).ready(function() {
    // 導航列載入
    $("#navbar-container").load("nvbar.html", function(response, status, xhr) {
        if (status == "error") {
            $("#navbar-container").html("載入發生錯誤: " + xhr.status + " " + xhr.statusText);
            return;
        }
        
        // 初始化使用者資訊
        initializeUserInfo();
    });

    // 登出功能
$(document).on('click', '#logoutBtn', function() {
if (confirm('確定要登出？')) {
handleLogout();
}
});

    // 下載 CSV 功能
   	$(document).on('click', '#downloadCsvBtn', function() {
		
		//$('#downloadCsvBtn').on('click', function() {
        console.log('下載按鈕被點擊');
        
        $.ajax({
            url: '/ExchangeHub/api3/download/csv',
            method: 'GET',
            xhrFields: { 
                responseType: 'blob' 
            },
            success: function(data, status, xhr) {
                const filename = xhr.getResponseHeader('Content-Disposition')
                    ?.split('filename=')[1] || 'transactions.csv';
                
                const blob = new Blob([data], { type: 'text/csv;charset=utf-8;' });
                const link = document.createElement('a');
                link.href = window.URL.createObjectURL(blob);
                link.download = filename;
                
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);

                alert('CSV 檔案下載成功');
            },
            error: function(xhr, status, error) {
                console.error('下載失敗:', error);
                alert('下載交易紀錄失敗');
            }
        });
    });
});

// 初始化使用者資訊
function initializeUserInfo() {
    try {
        const userInfo = JSON.parse(sessionStorage.getItem('user_info'));
        
        if (!userInfo || !userInfo.username) {
            window.location.href = 'login.html';
            return;
        }
        
        $('#userDisplayName').text(userInfo.username);
    } catch (error) {
        console.error('初始化使用者資訊時發生錯誤:', error);
        window.location.href = 'login.html';
    }
}

// 登出處理
function handleLogout() {
    try {
        const tokenData = JSON.parse(sessionStorage.getItem('auth_token'));
        const token = tokenData?.value;

        // 清除 session 資料
        sessionStorage.clear();

        // 發送登出請求
        $.ajax({
            url: '/ExchangeHub/api3/auth/logout',
            type: 'POST',
            headers: { 
                'Authorization': `Bearer ${token}` 
            },
            complete: function() {
                window.location.href = 'login.html';
            }
        });
    } catch (error) {
        console.error('登出時發生錯誤:', error);
        window.location.href = 'login.html';
    }
}

//// 【新增】應用程式管理器，統一管理所有功能
//const AppManager = {
//    // 初始化方法
//    init() {
//       	this.loadNavbar();
//        this.setupDownloadButton(); // 【新增】設定下載按鈕監聽器
//    },
//
//    // 載入導航列
//    loadNavbar() {
//        $("#navbar-container").load("nvbar.html", (response, status, xhr) => {
//            if (status === "error") {
//                var msg = "載入發生錯誤: ";
//                $("#navbar-container").html(msg + xhr.status + " " + xhr.statusText);
//                return;
//            }
//            // 初始化導航列相關功能
//            this.initializeNavbar();
//        });
//    },
//
//    // 【修改】初始化導航列功能
//    initializeNavbar() {
//        try {
//            const userInfo = JSON.parse(sessionStorage.getItem('user_info'));
//            
//            // 檢查使用者資訊
//            if (!userInfo || !userInfo.username) {
//                this.redirectToLogin();
//                return;
//            }
//
//            // 顯示使用者名稱
//            $('#userDisplayName').text(userInfo.username);
//
//            // 註冊登出事件
//            $('#logoutBtn').click(() => {
//                if (confirm('確定要登出？')) {
//                    this.handleLogout();
//                }
//            });
//        } catch (error) {
//            console.error('初始化導航欄時發生錯誤:', error);
//            this.redirectToLogin();
//        }
//    },
//
//    // 【新增】設定下載按鈕功能
//    setupDownloadButton() {
//        $('#downloadCsvBtn').on('click', (e) => {
//            console.log('123456');
//            
//            //e.preventDefault(); // 防止預設行為
//            this.downloadCsv();
//        });
//    },
//
//    // 【新增】CSV 下載邏輯
//    downloadCsv() {
//        const $btn = $('#downloadCsvBtn');
//        
//        // 禁用按鈕，防止重複點擊
//        $btn.prop('disabled', true)
//            .text('下載中...');
//
//        $.ajax({
//            url: '/ExchangeHub/api3/download/csv',
//            method: 'GET',
//            xhrFields: { 
//                responseType: 'blob' 
//            },
//            beforeSend: () => {
//                console.log('準備發送下載請求');
//            },
//            success: (data, status, xhr) => {
//                console.log('成功接收 CSV 檔案');
//                
//                // 取得檔案名稱，若無法取得則使用預設名稱
//                const filename = xhr.getResponseHeader('Content-Disposition')
//                    ?.split('filename=')[1] || 'transactions.csv';
//                
//                // 建立下載連結
//                const blob = new Blob([data], { type: 'text/csv;charset=utf-8;' });
//                const link = document.createElement('a');
//                link.href = window.URL.createObjectURL(blob);
//                link.download = filename;
//                
//                // 觸發下載
//                document.body.appendChild(link);
//                link.click();
//                document.body.removeChild(link);
//
//                // 顯示成功訊息
//                this.showNotification('CSV 檔案下載成功', 'success');
//            },
//            error: (xhr, status, error) => {
//                console.error('下載失敗:', error);
//                this.showNotification('下載交易紀錄失敗', 'error');
//            },
//            complete: () => {
//                // 還原按鈕狀態
//                $btn.prop('disabled', false)
//                    .text('一鍵下載歷史清單CSV');
//            }
//        });
//    },
//
//    // 【修改】處理登出邏輯
//    handleLogout() {
//        try {
//            const tokenData = JSON.parse(sessionStorage.getItem('auth_token'));
//            const token = tokenData?.value;
//
//            // 清除所有 session 資料
//            sessionStorage.clear();
//
//            // 發送登出請求
//            $.ajax({
//                url: '/ExchangeHub/api3/auth/logout',
//                type: 'POST',
//                headers: { 
//                    'Authorization': `Bearer ${token}` 
//                },
//                complete: () => {
//                    this.redirectToLogin();
//                }
//            });
//        } catch (error) {
//            console.error('登出時發生錯誤:', error);
//            this.redirectToLogin();
//        }
//    },
//
//    // 【新增】通用通知方法
//    showNotification(message, type) {
//        const notification = $(`
//            <div class="notification ${type}">
//                ${message}
//            </div>
//        `).appendTo('body');
//
//        setTimeout(() => {
//            notification.fadeOut(300, () => notification.remove());
//        }, 3000);
//    },
//
//    // 重新導向到登入頁面
//    redirectToLogin() {
//        window.location.href = 'login.html';
//    }
//};
//
//// 文件準備就緒時初始化應用程式
//$(document).ready(() => {
//    AppManager.init();
//});
//
//$('#downloadCsvBtn').on('click',()=>{
//	console.log('qqqqqqqqqq')
//})

 