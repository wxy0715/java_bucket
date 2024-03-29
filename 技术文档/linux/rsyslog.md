> 文档介绍:https://blog.csdn.net/u013168176/article/details/95043807
>
> https://www.cnblogs.com/skyofbitbit/p/3674664.html

# 配置

```sh
# 查看指定优先级（及其以上级别）的日志，共有8级
0: emerg: system is unusable
1: alert: action must be taken immediately
2: Critical: critical conditions
3: Error: error conditions
4: Warning: warning conditions
5: notice: normal but significant condition
6: Informational: informational messages
7: Debug: debug-level messages

Numerical             Facility
0             kernel messages     #内核信息
1             user-level messages #用户进程信息
2             mail system         #电子邮件相关信息
3             system daemons      #后台进程相关信息
4             security/authorization messages (note 1)
5             messages generated internally by syslogd
6             line printer subsystem  #打印服务相关信息
7             network news subsystem  #新闻组服务器信息
8             UUCP subsystem  # uucp生成的信息
9             clock daemon (note 2) 
10            security/authorization messages (note 1) #包括特权信息如用户名在内的认证活动
11             FTP daemon  # ftp信息
12             NTP subsystem #ntp信息
13             log audit (note 1) 
14             log alert (note 1)
15             clock daemon (note 2) 
16             local use 0  (local0) #本地用户信息
17             local use 1  (local1) #本地用户信息
18             local use 2  (local2) #本地用户信息
19             local use 3  (local3) #本地用户信息
20             local use 4  (local4) #本地用户信息
21             local use 5  (local5) #本地用户信息
22             local use 6  (local6) #本地用户信息
23             local use 7  (local7) #本地用户信息

facility.priority(设备.优先级)
```



# 管理FTP日志

```sh
yum install -y vsftpd lftp
vim /etc/vsftpd/vsftpd.conf
最底部加上这个
syslog_enable=YES
关闭格式化
xferlog_std_format=NO
# 创建用户
useradd ftp1
passwd ftp1
# 修改rsyslog配置
vim /etc/rsyslog.conf
ftp.*                                                   /var/log/ftp.log # 管理ftp日志

# 本地连接
lftp ip地址 
然后登陆-输入命令 login ftp1
然后就可以查看日志
```

# 管理ssh连接日志

```sh
vim /etc/ssh/sshd_config
# Logging
SyslogFacility local1  # 修改默认的日志载体
# 重启 systemctl restart sshd

vim /etc/rsyslog.conf
*.info;mail.none;authpriv.none;cron.none;local1.none                /var/log/messages # ;local1.none 忽略ssh的日志
# The authpriv file has restricted access.
authpriv.*;local1.none                                              /var/log/secure  #;local1.none 忽略ssh的日志

local1.*                                                /var/log/ssh.log  # 管理ssh的日志

# 其他机器 ssh 本机 然后 tail -f 查看日志即可
```



# 日志服务器



```sh
# Provides UDP syslog reception
$ModLoad imudp
$UDPServerRun 514

# Provides TCP syslog reception
$ModLoad imtcp
$InputTCPServerRun 514

local1.* @@81.69.172.101:514  # 一个@代表udp,两个@代表tcp  #把本机的local1的日志tcp转发给81.69.172.101日志服务器
```

# 自定义模板

```xml
vim /etc/rsyslog.conf
定义一个模板
$template DynFile, "/var/log/ssh-%HOSTNAME%.log"
//动态加载上面的模板
local1.* ?DynFile
```



# 日志轮转

## 参数

```yml
主要参数如下
compress 通过gzip 压缩转储以后的日志
nocompress 不需要压缩时，用这个参数
copytruncate 用于还在打开中的日志文件，把当前日志备份并截断
nocopytruncate 备份日志文件但是不截断
create mode owner group 转储文件，使用指定的文件模式创建新的日志文件
nocreate不建立新的日志文件
delaycompress和compress 一起使用时，转储的日志文件到下一次转储时才压缩
nodelaycompress覆盖delaycompress选项，转储并压缩
errors address 专储时的错误信息发送到指定的Email 地址
ifempty即使是空文件也转储，是缺省选项。
notifempty如果是空文件的话，不转储
mail address 把转储的日志文件发送到指定的E-mail 地址
nomail转储时不发送日志文件
olddirdirectory 转储后的日志文件放入指定的目录，必须和当前日志文件在同一个文件系统
noolddir转储后的日志文件和当前日志文件放在同一个目录下
prerotate/endscript在转储以前需要执行的命令可以放入这个对，这两个关键字必须单独成行
postrotate/endscript在转储以后需要执行的命令可以放入这个对，这两个关键字必须单独成行
daily 指定转储周期为每天
weekly 指定转储周期为每周
monthly 指定转储周期为每月
size 大小指定日志超过多大时，就执行日志转储
rotate count 指定日志文件删除之前转储的次数，0 指没有备份，5 指保留5 个备份
Missingok如果日志不存在，提示错误
Nomissingok如果日志不存在，继续下一次日志，不提示错误
```



> 当日志过大时,需要对日志进行拷贝

**看包存在与否:rpm -q logrotate,查看位置:rpm -ql logrotate**

```shell
# see "man logrotate" for details
# rotate log files weekly
weekly  # 每周保存一次

# keep 4 weeks worth of backlogs
rotate 4 # 每次保存4周的日志

# create new (empty) log files after rotating old ones
create 

# use date as a suffix of the rotated file
dateext  #日期作为后缀

# uncomment this if you want your log files compressed
#compress

# RPM packages drop log rotation information into this directory
include /etc/logrotate.d

# no packages own wtmp and btmp -- we'll rotate them here
/var/log/wtmp {
    monthly
    create 0664 root utmp
        minsize 1M  # 文件大于1m 而且周期到了才会轮转
    rotate 1
}

/var/log/btmp {
    missingok  # 日志文件不存在 不报错
    monthly
    create 0600 root utmp
    rotate 1
}
```



## SSH日志轮转

```sh
vim /etc/ssh/sshd_config
# Logging
SyslogFacility local1  # 修改默认的日志载体

vim /etc/rsyslog.conf
*.info;mail.none;authpriv.none;cron.none;local1.none                /var/log/messages # ;local1.none 忽略ssh的日志
# The authpriv file has restricted access.
authpriv.*;local1.none                                              /var/log/secure  #;local1.none 忽略ssh的日志
local1.*                                                /var/log/ssh.log  # 管理ssh的日志

# 修改轮转周期
vim /etc/logrotate.d/sshd
/var/log/sshd.log {
	missingok
	daily
	rotate 5
	size 5M
}
# 或者
vim /etc/logrotate.conf
/var/log/sshd.log {
	missingok
	daily
	rotate 5
	size 5M  # 到了5M就会轮转不管周期到了没有
}
```



## FTP日志轮转

```sh
vim /etc/vsftpd/vsftpd.conf
最底部加上这个
syslog_enable=YES
关闭格式化
xferlog_std_format=NO

vim /etc/rsyslog.conf
ftp.*                                                   /var/log/ftp.log # 管理ftp日志

# 修改轮转周期
vim /etc/logrotate.d/ftp
/var/log/ftp.log  {
	missingok
	daily
	rotate 5
	size 5M
}
# 或者
vim /etc/logrotate.conf
/var/log/ftp.log  {
	missingok
	daily
	rotate 5
	size 5M  # 到了5M就会轮转不管周期到了没有
}
```



# 存储到MySQL

```
定义输出模块，把日志记录于其他位置
数据库和rsyslog服务器在同一台主机之上
(1) 在rsyslog服务器上安装mysql模块相关的程序包
	yum install -y rsyslog-mysql
安装该软件包会自动生成数据库脚本
	[root@centos7 ~]# rpm -ql rsyslog-mysql 
	/usr/lib64/rsyslog/ommysql.so
	/usr/share/doc/rsyslog-8.24.0/mysql-createDB.sql
(2) 为rsyslog创建数据库及表；
    mysql -uroot -p < /usr/share/doc/rsyslog-7.4.7/mysql-createDB.sql
(3) 在mysqlserver上授权rsyslog能连接至当前服务器
    [root@centos7 ~]# mysql -uroot -p
    GRANT ALL privileges ON Syslog.* TO 'USER'@'%' IDENTIFIED BY '';
    GRANT ALL privileges ON Syslog.* TO 'rsyslog'@'127.0.0.1' IDENTIFIED BY '';  
    flush privileges;
(4) 配置rsyslog将日志保存到mysql中
    vim /etc/rsyslog.conf
    $ModLoad ommysql       加载模块
    #### RULES ####
    *.info;mail.none;authpriv.none;cron.none                :ommysql:localhost,Syslog,rsyslog,centos
    重启服务生效
    systemctl restart rsyslog
(5) 进入数据库查看生成的数据
    [root@centos7 ~]# mysql -uroot -p
    MariaDB [(none)]> use Syslog; 
    MariaDB [(none)]> show tables; 
    MariaDB [Syslog]> select * from SystemEvents\G;

(6) 字段介绍
                ID: 3707				//ID
        CustomerID: NULL				//自定义ID
        ReceivedAt: 2022-03-09 09:36:01 //系统收到日志时间
DeviceReportedTime: 2022-03-09 09:36:01 // 设备发送日志时间
          Facility: 3					// 设备
          Priority: 6					//权重
          FromHost: VM-12-12-centos     //源地址
           Message:  Started Session 564478 of user root. 
        NTSeverity: NULL
        Importance: NULL
       EventSource: NULL
         EventUser: NULL
     EventCategory: NULL
           EventID: NULL
   EventBinaryData: NULL
      MaxAvailable: NULL
         CurrUsage: NULL
          MinUsage: NULL
          MaxUsage: NULL
        InfoUnitID: 1
         SysLogTag: systemd:
      EventLogType: NULL
   GenericFileName: NULL
          SystemID: NULL
```

# Loganalyzer日志展示

> https://loganalyzer.adiscon.com/download/
>
> 先把包上传到linux /opt下然后解压 
>
> (1)使用yum安装方式快速部署AMP
> 	yum -y install httpd，php，php-mysql，php-gd
> (2) 安装LogAnalyzer
> 	tar xfloganalyzer-4.1.2.tar.gz
> 	cp -a loganalyzer-4.1.2/src/ var/www/html/loganalyzer
> 	cd /var/www/html/loganalyzer
> 	touch config.php
> 	chmod 666 config.php
> (3) 配置loganalyzer
> 	systemctl restart httpd.service
> http://HOST/loganalyzer

# java实现

```xml
		<!--syslog-->
		<dependency>
			<groupId>org.graylog2</groupId>
			<artifactId>syslog4j</artifactId>
			<version>0.9.60</version>
		</dependency>
		<dependency>
			<groupId>com.vaadin.external.google</groupId>
			<artifactId>android-json</artifactId>
			<version>0.0.20131108.vaadin1</version>
			<scope>compile</scope>
		</dependency>
```



```java
package com.wxy.utils.syslog;


import org.graylog2.syslog4j.Syslog;
import org.graylog2.syslog4j.SyslogConstants;
import org.graylog2.syslog4j.SyslogIF;
import org.graylog2.syslog4j.impl.net.udp.UDPNetSyslogConfig;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.Date;

public class SyslogSend {
	private static final String ENC = "utf-8";
	private static final String HOST = "127.0.0.1";
	private static final int PORT = 514;

	public static void main(String[] args) throws JSONException {
		new SyslogSend().send(System.currentTimeMillis() + "", "这是一条测试日志");
	}

	public void send(String traceId, String msg) {
		SyslogIF syslog = Syslog.getInstance(SyslogConstants.UDP);
		UDPNetSyslogConfig udpNetSyslog = (UDPNetSyslogConfig)syslog.getConfig();
		udpNetSyslog.setHost(HOST);
		udpNetSyslog.setPort(PORT);
		udpNetSyslog.setSendLocalName(true);//携带本机主机名
		udpNetSyslog.setCacheHostAddress(false);
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("traceId", traceId);
			jsonObject.put("time", new Date().toString().substring(4, 20));
			jsonObject.put("message", msg);
			jsonObject.put("level", 0);
			syslog.log(0, URLDecoder.decode(jsonObject.toString(), ENC));

			jsonObject.put("level", 1);
			syslog.log(1, URLDecoder.decode(jsonObject.toString(), ENC));

			jsonObject.put("level", null);
			jsonObject.put("type", "info");
			syslog.info(URLDecoder.decode(jsonObject.toString(), ENC));

			jsonObject.put("type", "error");
			syslog.error(URLDecoder.decode(jsonObject.toString(), ENC));

			jsonObject.put("type", "warn");
			syslog.warn(URLDecoder.decode(jsonObject.toString(), ENC));
		} catch (Exception e) {
			System.out.println("send exception " + e.getMessage());
		}
	}
        /* syslog.log——发送信息到服务器，数字表示日志级别 范围为0~7的数字编码，表示了事件的严重程度。0最高，7最低
        * syslog为每个事件赋予几个不同的优先级：
        0 LOG_EMERG：紧急情况，需要立即通知技术人员。
        1 LOG_ALERT：应该被立即改正的问题，如系统数据库被破坏，ISP连接丢失。
        2 LOG_CRIT：重要情况，如硬盘错误，备用连接丢失。
        3 LOG_ERR：错误，不是非常紧急，在一定时间内修复即可。
        4 LOG_WARNING：警告信息，不是错误，比如系统磁盘使用了85%等。
        5 LOG_NOTICE：不是错误情况，也不需要立即处理。
        6 LOG_INFO：情报信息，正常的系统消息，比如骚扰报告，带宽数据等，不需要处理。
        7 LOG_DEBUG：包含详细的开发情报的信息，通常只在调试一个程序时使用。
        */

}
```

