package com.wxy.ftpClient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FTPClient配置类，封装了FTPClient的相关配置
 * @author wxy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FTPClientConfigure {
	private String host;
	private int port;
	private String username;
	private String password;
	private String passiveMode;
	private String encoding;
	private int clientTimeout;
	// 线程数量
	private int threadNum;
	// 传输文件类型
	private int transferFileType;
	// 重命名上传
	private boolean renameUploaded;
	// 尝试次数
	private int retryTimes;
}
