package com.exception;
//基礎異常類
public class BaseException extends RuntimeException {
 private final String code;
 private final String message;
 
 public BaseException(String code, String message) {
     super(message);
     this.code = code;
     this.message = message;
 }
 
 public String getCode() { return code; }
 
 @Override
 public String getMessage() { return message; }
}






