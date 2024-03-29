package ftpClient;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool.PoolableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

public class FtpClientFactory implements PoolableObjectFactory<FTPClient> {
	private static Logger logger = LoggerFactory.getLogger("file");
	private  FTPClientConfigure config;
	//给工厂传入一个参数对象，方便配置FTPClient的相关参数
	public FtpClientFactory(FTPClientConfigure config){
		this.config=config;
	}
	/* (non-Javadoc)
	 * @see org.apache.commons.pool.PoolableObjectFactory#makeObject()
	 */
	@Override
	public FTPClient makeObject() throws Exception {
		FTPClient ftpClient = new FTPClient();
		ftpClient.setConnectTimeout(config.getClientTimeout());
		try {
			ftpClient.connect(config.getHost(), config.getPort());
			int reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
				logger.warn("FTPServer refused connection");
				return null;
			}
			boolean result = ftpClient.login(config.getUsername(), config.getPassword());
			if (!result) {
				throw new Exception("ftpClient登陆失败! userName:" + config.getUsername() + " ; password:" + config.getPassword());
			}
			ftpClient.setFileType(config.getTransferFileType());
			ftpClient.setBufferSize(1024);
			ftpClient.setControlEncoding(config.getEncoding());
			if (config.getPassiveMode().equals("true")) {
				ftpClient.enterLocalPassiveMode();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ftpClient;
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.pool.PoolableObjectFactory#destroyObject(java.lang.Object)
	 */
	@Override
	public void destroyObject(FTPClient ftpClient) throws Exception {
		try {
			if (ftpClient != null && ftpClient.isConnected()) {
				ftpClient.logout();
			}
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			// 注意,一定要在finally代码中断开连接，否则会导致占用ftp连接情况
			try {
				ftpClient.disconnect();
			} catch (IOException io) {
				io.printStackTrace();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.pool.PoolableObjectFactory#validateObject(java.lang.Object)
	 */
	@Override
	public boolean validateObject(FTPClient ftpClient) {
		try {
			return ftpClient.sendNoOp();
		} catch (IOException e) {
			throw new RuntimeException("Failed to validate client: " + e, e);
		}
	}

	@Override
	public void activateObject(FTPClient ftpClient) throws Exception {
	}

	@Override
	public void passivateObject(FTPClient ftpClient) throws Exception {

	}
}
