package com.wxy.utils.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class JavaMailSender {
    @Autowired
    private JavaMailSenderImpl mailSender;

    public String sendMail(String mail){
        try {
            //简单的邮件信息(复杂邮件信息使用MimeMessageHelper对象)
            SimpleMailMessage msg = new SimpleMailMessage();
            //邮件发信人,可从配置文件获取
            msg.setFrom("2357191256@qq.com");
            //邮件收信人
            msg.setTo(mail);
            //邮件主题
            msg.setSubject("王星宇验证码");
            //创建一个6位数随机数
            String s = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
            //邮件内容
            msg.setText("miss you");
            mailSender.send(msg);
            return "send ok";
        } catch (Exception e) {
            return "send fild";
        }
    }

    public String sendMimeMail(String mail) {
        try {
            //创建MimeMessageHelper信息对象,true表⽰⽀持复杂类型
            MimeMessageHelper messageHelper =
                    new MimeMessageHelper(mailSender.createMimeMessage(),  true);
            //邮件发信⼈
            messageHelper.setFrom("2357191256@qq.com");
            //邮件收信⼈
            messageHelper.setTo(mail);
            //邮件主题
            messageHelper.setSubject("美女合集");
            String s = String.valueOf((int)((Math.random() * 9 + 1) * 100000));
            //邮件内容,6位数随机数
            messageHelper.setText("骗你的");
            //正式发送邮件
            mailSender.send(messageHelper.getMimeMessage());
            return "send ok";
        }  catch (Exception e) {
            return "send fail";
        }
    }

}
