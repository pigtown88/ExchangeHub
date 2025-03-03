package com.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class ResponseWrapper extends HttpServletResponseWrapper {
    private ByteArrayOutputStream capture;  //用於捕獲響應的輸出流 //錄影帶
    private ServletOutputStream output;  
    private PrintWriter writer;   
//有點像是看電視節目時，可以同時播放和錄製，讓節目部中斷
    public ResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);
        capture = new ByteArrayOutputStream();//初始化捕獲輸入，這是在內存捕獲所有寫入響應的數據(儲存所有錄製內容的地方)
        output = new ServletOutputStream() { // 自己自定義的輸出流，將數據寫入cature而不適直接寫在HTTP裡面(發送的同時順便錄製)
        	
            @Override
            public void write(int b) throws IOException {
                capture.write(b); //將數據寫入ByteArrayOutputStream，而不是寫到http裡面
            }

            @Override
            public boolean isReady() {
                return true; //總是準備好輸入數據
            }

            @Override
            public void setWriteListener(WriteListener listener) {
                // 無須實現非阻io的填寫 裡面不寫東西是因為我們是用的同步IO
            }
        };
        writer = new PrintWriter(capture);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return output;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return writer;
    }

    public String getCaptureAsString() throws IOException {
        writer.flush(); // // 確保所有數據都寫入到capture中
        return capture.toString("UTF-8");
    }

    public byte[] getCaptureAsBytes() {
        writer.flush(); // 確保所有數據都寫入到capture中
        return capture.toByteArray();
    }
}
