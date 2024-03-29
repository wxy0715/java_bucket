package ftpClient;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketPort extends Thread{
	final static int DEFAULT_BACKLOG = 50;
	final static PrintStream psDebug = System.out;
	Socket skControlClient;
	BufferedReader brClient;

	public ServerSocketPort(Socket skControlClient){
		this.skControlClient = skControlClient;
	}

	public static void main(String[] args) {
		try {
			ServerSocket ssControlClient = new ServerSocket(55211,DEFAULT_BACKLOG, InetAddress.getLocalHost());
			psDebug.println("Listening on port " + 55211);
			while (true) {
				Socket skControlClient = ssControlClient.accept();
				psDebug.println("New connection");
				new ServerSocketPort(skControlClient).start();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			DataInputStream input=new DataInputStream(skControlClient.getInputStream());
			byte[] buffer = new byte[20480];
			//消息长度
			int rlength=input.read(buffer, 0, 20480);
			System.out.println("接收的消息长度:"+rlength);
			//传输的实际byte[]
			byte[] buffer1 = new byte[rlength];
			for(int i=0;i<buffer1.length;i++){
				buffer1[i]=buffer[i];
			}

			String messageContent1=new String(buffer1,"GBK").toString().trim();
			System.out.println("接收的消息（gbk转码）："+messageContent1);

			String messageContent=new String(buffer,0,rlength).toString().trim();
			System.out.println("接收的消息："+messageContent);
			OutputStream outputStream = skControlClient.getOutputStream();
			outputStream.write("收到".getBytes());
			outputStream.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
