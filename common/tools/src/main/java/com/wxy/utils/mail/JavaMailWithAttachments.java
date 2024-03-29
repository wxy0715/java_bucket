package com.wxy.utils.mail;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.util.Date;
import java.util.Properties;

//222

public class JavaMailWithAttachments {
	//qq
	public static String myEmailSMTPHost = "smtp.qq.com";
	//授权码
	public static String sendEmailPwd = "";
    // 发件人的 邮箱 和 密码（替换为自己的邮箱和密码）
    public static String sendEmailAccount = "@qq.com";
	// 收件人邮箱（替换为自己知道的有效邮箱）
	public static String receiveMailAccount = "@qq.com";

    //网易163邮箱
/*	public static String myEmailSMTPHost = "smtp.163.com";
	public static String sendEmailPwd = "";
	// 发件人的 邮箱 和 密码（替换为自己的邮箱和密码）
	public static String sendEmailAccount = "1@163.com";
	// 收件人邮箱（替换为自己知道的有效邮箱）
	public static String receiveMailAccount = "@163.com";*/

    public static void main(String[] args) throws Exception {
        // 1. 创建参数配置, 用于连接邮件服务器的参数配置
        Properties props = new Properties();                    // 参数配置
        props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", myEmailSMTPHost);   // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");            // 需要请求认证
        
        final String smtpPort = "465";
        props.setProperty("mail.smtp.port", smtpPort);
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", smtpPort);

        // 开启 SSL 连接, 以及更详细的发送步骤请看上一篇: 基于 JavaMail 的 Java 邮件发送：简单邮件发送

        // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getInstance(props);
        session.setDebug(true);                                 // 设置为debug模式, 可以查看详细的发送 log

        // 3. 创建一封邮件
        MimeMessage message = createMimeMessage(session, sendEmailAccount, receiveMailAccount);

        // 也可以保持到本地查看
        // message.writeTo(file_out_put_stream);

        // 4. 根据 Session 获取邮件传输对象
        Transport transport = session.getTransport();

        // 5. 使用 邮箱账号 和 密码 连接邮件服务器
        //    这里认证的邮箱必须与 message 中的发件人邮箱一致，否则报错
        transport.connect(sendEmailAccount, sendEmailPwd);

        // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(message, message.getAllRecipients());
        // 7. 关闭连接
        transport.close();
    }

    /**
     * 创建一封复杂邮件（文本+图片+附件）
     */
    public static MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail) throws Exception {
		// 1. 创建邮件对象
		MimeMessage message = new MimeMessage(session);
		// 2. From: 发件人
		message.setFrom(new InternetAddress(sendMail, "王星宇", "UTF-8"));
		// 3. To: 收件人（可以增加多个收件人、抄送、密送）
		message.addRecipient(RecipientType.TO, new InternetAddress(receiveMail, "帅哥美女", "UTF-8"));
		// 4. Subject: 邮件主题
		message.setSubject("您收到一封来自王星宇的邮件,请查收", "UTF-8");

		// 5. 创建图片“节点”
		MimeBodyPart imagePart = new MimeBodyPart();
		DataHandler imageDataHandler = new DataHandler(new FileDataSource("D:/我的文档/Pictures/smallfb5eb76bfad671a169013c54fb79e5d51595173351.jpg")); // 读取本地文件
		imagePart.setDataHandler(imageDataHandler);                   // 将图片数据添加到“节点”
		imagePart.setContentID("image_fairy_tail");     // 为“节点”设置一个唯一编号（在文本“节点”将引用该ID）
		// 6. 创建文本“节点”
		MimeBodyPart textPart = new MimeBodyPart();
		//    这里添加图片的方式是将整个图片包含到邮件内容中, 实际上也可以以 http 链接的形式添加网络图片
		textPart.setContent("<img src='cid:image_fairy_tail'/>",
						"text/html;charset=UTF-8");
		
		// 7. （文本+图片）设置 文本 和 图片 “节点”的关系（将 文本 和 图片 “节点”合成一个混合“节点”）
		MimeMultipart mm_text_image = new MimeMultipart();
		mm_text_image.addBodyPart(textPart);
		mm_text_image.addBodyPart(imagePart);
		mm_text_image.setSubType("related");    // 关联关系
		
		// 8. 将 文本+图片 的混合“节点”封装成一个普通“节点”
		MimeBodyPart text_image = new MimeBodyPart();
		text_image.setContent(mm_text_image);
		// 9. 创建附件“节点”
		MimeBodyPart attachment = new MimeBodyPart();
		DataHandler attDataHandler = new DataHandler
					(new FileDataSource("D:/我的文档/Pictures/你想要的.txt"));  // 读取本地文件
		attachment.setDataHandler(attDataHandler);                                             // 将附件数据添加到“节点”
		attachment.setFileName(MimeUtility.encodeText(attDataHandler.getName()));              // 设置附件的文件名（需要编码）
		// 10. 设置（文本+图片）和 附件 的关系（合成一个大的混合“节点” / Multipart ）
		MimeMultipart mm = new MimeMultipart();
		mm.addBodyPart(text_image);
		mm.addBodyPart(attachment);     // 如果有多个附件，可以创建多个多次添加
		mm.setSubType("mixed");         // 混合关系
		
		// 11. 设置整个邮件的关系（将最终的混合“节点”作为邮件的内容添加到邮件对象）
		message.setContent(mm);

		// 12. 设置发件时间
		message.setSentDate(new Date());
		
		// 13. 保存上面的所有设置
		message.saveChanges();
		
		return message;
    }

}
