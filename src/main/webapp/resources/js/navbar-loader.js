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

 