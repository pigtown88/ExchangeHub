package com.exception;

//DAO層異常
public class DAOException extends BaseException {
	public DAOException(String code, String message) {
		super("D_" + code, message);
	}
}
