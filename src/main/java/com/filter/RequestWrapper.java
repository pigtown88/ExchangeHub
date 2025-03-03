package com.filter;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
/*有點類似一個神奇影印機，可以邊預覽 邊製作一個副本給你，部會影響你的預覽
 * 讀取並應用整個請求內容
 * 把內容存在body中
*/

//繼承HttpServletRequestWrapper，自訂義包裝器，以便多次讀取請求體
public class RequestWrapper extends HttpServletRequestWrapper {
    private final String body; //儲存請求體的內容

    
    //構造出1個方法，接收HttpServletRequest作為參數
    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request); //調用父類方法，傳遞原始變量
        
        //增加內容的提醒
        String contentType = request.getContentType();
        if (contentType != null && contentType.contains("multipart/form-data")) {
			//對於檔案上傳的請求，做特殊的處理
    		this.body ="";
    		return;
		}
        
        
        StringBuilder stringBuilder = new StringBuilder(); //使用string來收集請求體
        BufferedReader bufferedReader = null; //初始化bufferedReader為null

        try {
            InputStream inputStream = request.getInputStream(); //從原始請求中或輸入流
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                
                char[] charBuffer = new char[128]; //創建一個字符緩衝區
                int bytesRead = -1; //讀取的自結初始化為-1，代表末尾，可確保執行一次
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) { //循環讀取輸入流數據到沒數據為止
                    stringBuilder.append(charBuffer, 0, bytesRead);  //從輸出流讀取數據並儲存
                }
            }
        } finally {
            if (bufferedReader != null) {//循環讀取輸入流數據到沒數據為止
                bufferedReader.close(); //關閉資源
            }
        }
        body = stringBuilder.toString(); //轉換成為字符串
    }
    
    //提供讀取請求體的bufferedReader
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(body.getBytes())));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
        
        ServletInputStream servletInputStream = new ServletInputStream() {
            public int read() throws IOException {
                return byteArrayInputStream.read();   //從byteArrayInputStream中讀取數據
            }

            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0; //確認是否已經讀取完成
            }

            @Override
            public boolean isReady() {
                return true;  //好像是讓數據可以被讀取的
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new RuntimeException("Not implemented");
            }
        };
        return servletInputStream;
    }
//查看複印件的方法
    public String getBody() {
        return this.body;  //返回請求體的內容
    }
}
