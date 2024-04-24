package com.wxy.ftpClient;

import org.apache.commons.net.ftp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.SocketException;

/**
 * ftp工具类
 * @author wangxingyu
 * @date 2024/04/24 16:32:24
 */
public class FtpUtils {
	//打印日志使用
	private static final Logger log = LoggerFactory.getLogger(FtpUtils.class);

	/**
	 * 获取FTPClient对象
	 *
	 * @param ftpHost
	 *            FTP主机服务器
	 * @param ftpPassword
	 *            FTP 登录密码
	 * @param ftpUserName
	 *            FTP登录用户名
	 * @param ftpPort
	 *            FTP端口 默认为21
	 * @return
	 */
	public static FTPClient getFTPClient(String ftpHost, String ftpUserName, String ftpPassword, int ftpPort) {
		FTPClient ftpClient = null;
		try {
			//创建一个ftp客户端
			ftpClient = new FTPClient();
			// 连接FTP服务器
			ftpClient.connect(ftpHost, ftpPort);
			// 登陆FTP服务器
			ftpClient.login(ftpUserName, ftpPassword);

			if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
				log.info("未连接到FTP，用户名或密码错误。");
				ftpClient.disconnect();
			} else {
				log.info("FTP连接成功。");
			}
		} catch (SocketException e) {
			e.printStackTrace();
			log.info("FTP的IP地址可能错误，请正确配置。");
		} catch (IOException e) {
			e.printStackTrace();
			log.info("FTP的端口错误,请正确配置。");
		}
		return ftpClient;
	}

	/**
	 * 下载文件
	 *
	 * @param ftpHost ftp服务器地址
	 * @param ftpUserName anonymous匿名用户登录，不需要密码。administrator指定用户登录
	 * @param ftpPassword 指定用户密码
	 * @param ftpPort ftp服务员器端口号
	 * @param ftpPath  ftp文件存放物理路径
	 * @param localPath 本地存储文件的路径
	 * @param fileName 文件名称
	 */
	public static void downloadFile(String ftpHost, String ftpUserName, String ftpPassword, int ftpPort, String ftpPath, String localPath, String fileName) {

		FTPClient ftpClient = null;

		try {
			ftpClient = getFTPClient(ftpHost, ftpUserName, ftpPassword, ftpPort);
			ftpClient.setControlEncoding("UTF-8"); // 中文支持
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();
			ftpClient.changeWorkingDirectory(ftpPath);

			File localFile = new File(localPath + File.separatorChar + fileName);
			OutputStream os = new FileOutputStream(localFile);
			ftpClient.retrieveFile(fileName, os);
			os.close();
			ftpClient.logout();

		} catch (FileNotFoundException e) {
			log.error("没有找到" + ftpPath + "文件");
			e.printStackTrace();
		} catch (SocketException e) {
			log.error("连接FTP失败.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			log.error("文件读取错误。");
			e.printStackTrace();
		}
	}

	/**
	 * 上传文件
	 *
	 * @param ftpHost ftp服务器地址
	 * @param ftpUserName anonymous匿名用户登录，不需要密码。administrator指定用户登录
	 * @param ftpPassword 指定用户密码
	 * @param ftpPort ftp服务员器端口号
	 * @param ftpPath  ftp文件存放物理路径
	 * @param fileName 文件路径
	 * @param input 文件输入流，即从本地服务器读取文件的IO输入流
	 */
	public static void uploadFile(String ftpHost, String ftpUserName, String ftpPassword, int ftpPort, String ftpPath, String fileName,InputStream input){
		FTPClient ftp=null;
		try {
			ftp=getFTPClient(ftpHost, ftpUserName, ftpPassword, ftpPort);

			ftp.makeDirectory(ftpPath);
			ftp.changeWorkingDirectory(ftpPath);

			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			fileName=new String(fileName.getBytes("GBK"),"iso-8859-1");
			ftp.storeFile(fileName, input);
			input.close();
			ftp.logout();
			System.out.println("upload succes!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		FTPClient ftpClient = getFTPClient("101.43.60.8", "ftpuser", "wxy0715..", 21);
		ftpClient.changeWorkingDirectory("/opt");
		ftpClient.enterLocalPassiveMode();
		//由于apache不支持中文语言环境，通过定制类解析中文日期类型
		ftpClient.configure(new FTPClientConfig("com.zznode.tnms.ra.c11n.nj.resource.ftp.UnixFTPEntryParser"));
		FTPFile[] files = ftpClient.listFiles();
		System.out.println(files.length);
	}
}