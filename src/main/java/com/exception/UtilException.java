package com.exception;

//工具層異常
public class UtilException extends BaseException {
public UtilException(String code, String message) {
   super("U_" + code, message);
}
}