package com.wxy.utils.file;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 * @author wxy
 * @description 文件上传下载流
 * @data 2021/1/14
 */

public class FileIoUtils {

	/**根据文件路径获取文件**/
	public static File getFileByName(String fileName){
		File file = new File(fileName);
		if(!file.exists()) {
			System.out.println("文件不存在");
			return null;
		}
		return file;
	}

	/**
	 *  根据文件名获取文件,不存在创建
	 * @param fileName     要上传的文件
	 * @param number    0文件,1目录
	 * @return  file   文件
	 * @throws IOException
	 */
	public static File getFileByNameCreate(String fileName,int number) throws IOException {
		File file = new File(fileName);
		if(!file.exists()) {
			if (number == 0) {
				return mkdirFiles(fileName);
			} else {
				file.mkdirs();
				return file;
			}
		}
		return file;
	}

	/**
	 * 根据文件获取文件,不存在创建
	 * @param file     要上传的文件
	 * @param number    0文件,1目录
	 * @return  file   文件
	 * @throws IOException
	 */
	public static File getFile(File file,int number) throws IOException {
		if(!file.exists()) {
			if (number == 0) {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
				return file;
			} else {
				file.mkdirs();
				return file;
			}
		}
		return file;
	}

	/**
	 * 根据filePath创建相应的目录
	 * @param filePath      要创建的文件路经
	 * @return  file        文件
	 * @throws IOException
	 */
	public static File mkdirFiles(String filePath) throws IOException {
		File file = new File(filePath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if (!file.exists()) {
			file.createNewFile();
		} else {
			file.delete();
			file.createNewFile();
		}
		return file;
	}

	/**
	 * 上传文件到服务器(普通Buffer字节方式读取)
	 * @param file     要上传的文件
	 * @param toFile    目标文件
	 * @return  file   文件
	 * @throws IOException
	 */
	public static void uploadBufferedByte(File file,File toFile) throws IOException {
		//创建文件输出流
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(toFile));
		//创建文件输入流
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		byte[] buffer = new byte[1024];
		int result;
		while ((result = bufferedInputStream.read(buffer)) != -1) {
			bufferedOutputStream.write(buffer, 0, result);
		}
		close(bufferedInputStream);
		bufferedOutputStream.flush();
		close(bufferedOutputStream);
		System.out.println("上传文件"+file.getName()+"完成");
	}

	/**
	 * 上传文件到服务器(普通Buffer字符方式读取)
	 * @param file     要上传的文件
	 * @param toFile    目标文件
	 * @return  file   文件
	 * @throws IOException
	 */
	public static void uploadBufferedChar(File file,File toFile) throws IOException {
		//创建文件输出流
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(toFile));
		//创建文件输入流
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		String line = null;
		//因为每次调用readLine()都会返回一行数据，如果返回了一个空，条件则不满足，就退出循环了
		while((line=bufferedReader.readLine()) != null){
			bufferedWriter.write(line);
		}
		close(bufferedReader);
		bufferedWriter.flush();
		close(bufferedWriter);
		System.out.println("上传文件"+file.getName()+"完成");
	}

	/**
	 * @description 上传文件到服务器(NIOBuffered字节方式读取)
	 * @param file     要上传的文件
	 * @param toFile    目标文件
	 * @return  file   文件
	 * @throws IOException
	 */
	public static void uploadNIOBufferedByte(File file,File toFile) throws IOException {
		//创建文件输出流
		FileChannel out = new FileOutputStream(toFile).getChannel();
		//创建文件输入流
		FileChannel in = new FileInputStream(file).getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(16384);
		while(in.read(buffer) != -1){
			buffer.flip();
			while (buffer.hasRemaining()) {
				out.write(buffer);
			}
			buffer.clear();
		}
		close(in);
		close(out);
		System.out.println("上传文件"+file.getName()+"完成");
	}

	/**服务器上下载文件*/
	public static void download(File file, String contentType, HttpServletResponse response) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType(contentType);
		response.setHeader("Content-disposition", "attachment;filename=" + file.getName());
		InputStream stream = new FileInputStream(file);
		ServletOutputStream out = response.getOutputStream();
		byte[] buff = new byte[1024];
		int length = 0;
		while ((length = stream.read(buff)) > 0) {
			out.write(buff,0,length);
		}
		close(stream);
		out.flush();
		close(out);
	}

	/**服务器上下载文件-buffered*/
	public static void downloadBuffered(String path, String contentType, HttpServletResponse response) throws IOException {
		File file = new File(path);
		String filename = file.getName();
		HttpServletRequest httpServletRequest = SystemUtils.getHttpServletRequest();
		filename = setFileDownloadHeader(httpServletRequest,filename);
		InputStream fis = new BufferedInputStream(new FileInputStream(path));
		byte[] buffer = new byte[fis.available()];
		fis.read(buffer);
		close(fis);
		// 清空response
		response.reset();
		// 设置response的Header
		response.addHeader("Content-Disposition", "attachment;filename=" + filename);
		response.addHeader("Content-Length", "" + file.length());
		OutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
		response.setContentType(contentType);
		bufferedOutputStream.write(buffer);
		bufferedOutputStream.flush();
		close(bufferedOutputStream);
	}

	/**服务器上下载未知content-type文件-buffered*/
	public static void downBufferedNoContent(String path,HttpServletResponse response) throws IOException {
		File file = new File(path);
		String filename = file.getName();
		InputStream fis = new BufferedInputStream(new FileInputStream(path));
		byte[] buffer = new byte[fis.available()];
		fis.read(buffer);
		close(fis);
		// 清空response
		response.reset();
		// 设置response的Header
		response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
		response.addHeader("Content-Length", "" + file.length());
		OutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
		response.setContentType("application/octet-stream");
		bufferedOutputStream.write(buffer);
		bufferedOutputStream.flush();
		close(bufferedOutputStream);
	}

	/**
	 * 批量删除文件
	 * @param fileRealPathList 文件绝对路径（带文件名）
	 */
	public static void deleteFiles(List<String> fileRealPathList) throws IOException {
		if (!CollectionUtils.isEmpty(fileRealPathList)) {
			File file = null;
			for (String fileRealPath : fileRealPathList)
			{
				file = new File(fileRealPath);
				if (file.exists() && file.isFile()) {
					file.delete();
				}
			}
		}
	}

	/**
	 * 删除单个文件
	 * @param fileRealPath 文件绝对路径（带文件名）
	 */
	public static void deleteOneFile(String fileRealPath) throws IOException {
		File file =  new File(fileRealPath);
		if (file.exists() && file.isFile()) {
			file.delete();
		}
	}

	/** 关闭流**/
	public static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/** 文件copy--小文件适用**/
	public static void bufferedStreamCopy(File source, File target) {
		InputStream fin = null;
		OutputStream fout = null;
		try {
			fin = new BufferedInputStream(new FileInputStream(source));
			fout = new BufferedOutputStream(new FileOutputStream(target));
			byte[] buffer = new byte[1024];
			int result;
			while ((result = fin.read(buffer)) != -1) {
				fout.write(buffer, 0, result);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(fin);
			close(fout);
		}
	}

	/** 文件copy--大文件适用**/
	public static void nioBufferCopy(File source, File target) {
			FileChannel fin = null;
			FileChannel fout = null;
			try {
				fin = new FileInputStream(source).getChannel();
				fout = new FileOutputStream(target).getChannel();
				ByteBuffer buffer = ByteBuffer.allocate(8192);
				while (fin.read(buffer) != -1) {
					buffer.flip();//读模式到写模式,指针初始化
					while (buffer.hasRemaining()) {
						fout.write(buffer);
					}
					buffer.clear();//写模式到读模式,指针初始化
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				close(fin);
				close(fout);
			}
		}

	/** 文件copy--中等文件适用**/
	public static void nioTransferCopy(File source, File target) {
		FileChannel fin = null;
		FileChannel fout = null;
		try {
			fin = new FileInputStream(source).getChannel();
			fout = new FileOutputStream(target).getChannel();
			long transferred = 0L;
			long size = fin.size();
			while (transferred != size) {
				transferred += fin.transferTo(0, size, fout);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(fin);
			close(fout);
		}
	}

	/**
	 * 获取文件类型
	 * <p>
	 * 例如: ruoyi.txt, 返回: txt
	 *
	 * @param file 文件名
	 * @return 后缀（不含".")
	 */
	public static String getFileType(File file) {
		if (null == file) {
			return StringUtils.EMPTY;
		}
		return getFileType(file.getName());
	}

	/**
	 * 获取文件类型
	 * <p>
	 * 例如: ruoyi.txt, 返回: txt
	 *
	 * @param fileName 文件名
	 * @return 后缀（不含".")
	 */
	public static String getFileType(String fileName) {
		int separatorIndex = fileName.lastIndexOf(".");
		if (separatorIndex < 0)
		{
			return "";
		}
		return fileName.substring(separatorIndex + 1).toLowerCase();
	}

	/**
	 * 下载文件名重新编码
	 * @param request 请求对象
	 * @param fileName 文件名
	 * @return 编码后的文件名
	 */
	public static String setFileDownloadHeader(HttpServletRequest request, String fileName) throws UnsupportedEncodingException
	{
		final String agent = request.getHeader("USER-AGENT");
		String filename = fileName;
		if (agent.contains("MSIE"))
		{
			// IE浏览器
			filename = URLEncoder.encode(filename, "utf-8");
			filename = filename.replace("+", " ");
		}
		else if (agent.contains("Firefox"))
		{
			// 火狐浏览器
			filename = new String(fileName.getBytes(), "ISO8859-1");
		}
		else if (agent.contains("Chrome"))
		{
			// google浏览器
			filename = URLEncoder.encode(filename, "utf-8");
		}
		else
		{
			// 其它浏览器
			filename = URLEncoder.encode(filename, "utf-8");
		}
		return filename;
	}


}
