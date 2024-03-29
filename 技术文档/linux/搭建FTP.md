## 搭建手册

参考:https://www.cnblogs.com/guohongwei/p/10848698.html

## 文档手册

FTP阿里云手册操作API:https://developer.aliyun.com/article/319859?spm=a2c6h.13813017.0.dArticle738638.3e783109t4yxPp

FTP连接池:https://developer.aliyun.com/article/54707

## 常见错误

- ftp vsftpd 530 login incorrect 

```
1.密码错误。
2.检查/etc/vsftpd/vsftpd.conf配置
vim /etc/vsftpd/vsftpd.conf
看下面配置
local_enable=YES  
pam_service_name=vsftpd     //这里重要，有人说ubuntu是pam_service_name=ftp，可以试试
userlist_enable=YES 
3.检查/etc/pam.d/vsftpd
vim /etc/pam.d/vsftpd
注释掉
#auth    required pam_shells.so
4.重启
```

- ftpClient获取文件listFiles为空 

  ```
  ftpClient.changeWorkingDirectory(path);  
              ftpClient.enterLocalPassiveMode();  //开启被动模式  
              //由于apache不支持中文语言环境，通过定制类解析中文日期类型  
              ftpClient.configure(new FTPClientConfig("com.zznode.tnms.ra.c11n.nj.resource.ftp.UnixFTPEntryParser"));  
   FTPFile[] files = ftpClient.listFiles();
  ```

  