package com.controller;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.service.AuthenticationService;
import com.service.response.ServiceResult;
import com.exception.*;
import com.model.member.AuthErrorResponse;
import com.model.member.LoginRequest;
import com.model.member.TokenResponse;

@Path("/auth")
public class AuthController {
	private static final Logger logger = LogManager.getLogger(AuthController.class);
	private final AuthenticationService authService;

	public AuthController() {
		this.authService = new AuthenticationService();
	}

	@POST
	@Path("/login")
	@PermitAll
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(LoginRequest request) {
		logger.info("收到登入請求");

		// 基本請求驗證
		if (request == null) {
			logger.warn("請求體為空");
			return buildErrorResponse(Response.Status.BAD_REQUEST, "AT001", "INVALID_REQUEST", "請求體不能為空");
		}

		// 調用服務層處理登入
		ServiceResult<TokenResponse> result = authService.login(request);

		// 處理結果
		if (result.isSuccess()) {
			logger.info("用戶 {} 登入成功", request.getUsername());
			return Response.ok(result.getData()).build();
		} else {
			logger.warn("用戶 {} 登入失敗: {}", request.getUsername(), result.getMessage());
			return buildErrorResponse(Response.Status.UNAUTHORIZED, 
					"AT003", "LOGIN_FAILED",result.getMessage());
			 //"AT002", "LOGIN_FAILED", result.getMessage());
			// result.getCode() 這邊會進來service的狀態，可以決地說我要顯示service or controller!
            // result.getCode(), "LOGIN_FAILED", result.getMessage());
		}
	}

	@POST
	@Path("/logout")
	//@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout(@HeaderParam("Authorization") String token) {
		logger.info("收到登出請求");

		if (token == null || !token.startsWith("Bearer ")) {
			return buildErrorResponse(Response.Status.UNAUTHORIZED, "AT201", "INVALID_TOKEN_FORMAT", "無效的Token格式");
		}

		ServiceResult<Void> result = authService.logout(token.substring(7));

		if (result.isSuccess()) {
			logger.info("登出成功");
			return Response.ok().build();
		} else {
			logger.warn("登出失敗: {}", result.getMessage());
			return buildErrorResponse(Response.Status.UNAUTHORIZED, "AT202", "LOGOUT_FAILED", result.getMessage());
		}
	}

	@GET
	@Path("/validate")
	@Produces(MediaType.APPLICATION_JSON)
	public Response validateToken(@HeaderParam("Authorization") String token) {
		logger.info("收到Token驗證請求");

		if (token == null || !token.startsWith("Bearer ")) {
			return buildErrorResponse(Response.Status.UNAUTHORIZED, "AT201", "INVALID_TOKEN_FORMAT", "無效的Token格式");
		}

		ServiceResult<Boolean> result = authService.validateToken(token.substring(7));

		if (result.isSuccess() && Boolean.TRUE.equals(result.getData())) {
			logger.info("Token驗證成功");
			return Response.ok().build();
		} else {
			logger.warn("Token驗證失敗: {}", result.getMessage());
			return buildErrorResponse(Response.Status.UNAUTHORIZED, "AT202", "INVALID_TOKEN", result.getMessage());
		}
	}

	/**
	 * 構建錯誤響應
	 */
	private Response buildErrorResponse(Response.Status status, String code, String type, String message) {
		AuthErrorResponse error = new AuthErrorResponse(code, type, message);
		return Response.status(status).entity(error).build();
	}
}
