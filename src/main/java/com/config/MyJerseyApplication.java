package com.config;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.controller.DuplicateCheckController;
import com.dao.ExchangeTransactionDao;
import com.service.DuplicateCheckService;

//這個和webxml只要一個就好
@ApplicationPath("/api3") // API 的根路徑，前面需要添加專案名稱
public class MyJerseyApplication extends ResourceConfig {

//	public MyJerseyApplication() {
//
//
////		// 自動掃描並註冊 com.example 包中的所有資源類
////		packages("com.controller");
////		
////		
////		// 註冊 MultiPartFeature 以支援檔案上傳
////        register(MultiPartFeature.class);
////        
////        // 如果需要設定檔案上傳大小限制
////        property("jersey.config.multipart.bufferThreshold", 1024 * 1024); // 1MB buffer
////        property("jersey.config.multipart.maxContentSize", 10 * 1024 * 1024); // 10MB max file size
//
//	}
}