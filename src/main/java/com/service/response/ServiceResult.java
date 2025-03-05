package com.service.response;

import java.time.LocalDateTime;

public class ServiceResult<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    // 私有構造函數
    private ServiceResult() {
        this.timestamp = LocalDateTime.now();
    }

    // 成功，無數據
    public static <T> ServiceResult<T> success() {
        return success(null);
    }

    // 成功，有數據
    public static <T> ServiceResult<T> success(T data) {
        ServiceResult<T> result = new ServiceResult<>();
        result.success = true;
        result.code = "S_000";
        result.message = "操作成功";
        result.data = data;
        return result;
    }

    // 成功，有數據和消息
    public static <T> ServiceResult<T> success(T data, String message) {
        ServiceResult<T> result = success(data);
        result.message = message;
        return result;
    }

    // 失敗
    public static <T> ServiceResult<T> failure(String code, String message) {
        ServiceResult<T> result = new ServiceResult<>();
        result.success = false;
        result.code = code;
        result.message = message;
        return result;
    }

    // Getters
    public boolean isSuccess() { return success; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public LocalDateTime getTimestamp() { return timestamp; }
}