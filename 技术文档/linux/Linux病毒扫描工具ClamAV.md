### 简介

```
ClamAV（Clam AntiVirus）是Linux平台上的开源病毒扫描程序，主要应用于邮件服务器，采用多线程后台操作，可以自动升级病毒库。
```

### 安装

**安装epel软件源**

```bash
# 安装
yum install -y epel-release  
# 缓存 
yum clean all && yum makecache 
```

**安装clamav程序**

```bash
yum -y install clamav-server clamav-data clamav-update clamav-filesystem clamav clamav-scanner-systemd clamav-devel clamav-lib clamav-server-systemd 
```

### 配置SELinux(可忽略,一般都是关闭)

配置ClamAV权限

```text
setsebool -P antivirus_can_scan_system 1 
setsebool -P clamd_use_jit 1 
```

查看设置结果

```text
getsebool -a | grep antivirus 
antivirus_can_scan_system --> on 
antivirus_use_jit --> on 
```

### 四、配置ClamAV

**1.删除示列**

```text
sed -i -e "s/^Example/#Example/" /etc/clamd.d/scan.conf 
sed -i -e "s/^Example/#Example/" /etc/freshclam.conf 
```

**2.编辑配置文件**

```text
vim /etc/clamd.d/scan.conf 
```

**3.找到以下行**

```text
#LocalSocket /var/run/clamd.scan/clamd.sock 
```

- 删除`#`符号并保存您的更改

### 五、更新病毒库

```bash
freshclam 
```

病毒库保存位置：

```bash
/var/lib/clamav/daily.cvd 
/var/lib/clamav/main.cvd 
```

### 六、启动Clamd服务

```bash
sudo systemctl start clamd@scan
sudo systemctl enable clamd@scan
```

### 七、扫描病毒

`clamscan` 可用以扫描文件, 用户目录亦或是整个系统：

```bash
#扫描文件 
clamscan targetfile  

##递归扫描home目录，并且记录日志 
clamscan -r -i /home  -l /var/log/clamav.log  

##递归扫描home目录，将病毒文件删除，并且记录日志 
clamscan -r -i /home  --remove  -l /var/log/clamav.log  

##扫描指定目录，然后将感染文件移动到指定目录，并记录日志 
clamscan -r -i /home  --move=/tmp/clamav -l /var/log/clamav.log 
```

**说明:**

- `-r -i` 递归扫描目录
- `-l` 指定记录日志文件
- `--remove` 删除病毒文件
- `--move` 移动病毒到指定目录

### 1.重点扫描目录

```text
clamscan -r  -i /etc --max-dir-recursion=5 -l /var/log/clamav-etc.log

clamscan -r  -i /bin --max-dir-recursion=5 -l /var/log/clamav-bin.log

clamscan -r  -i /usr --max-dir-recursion=5 -l /var/log/clamav-usr.log

clamscan -r  -i /var --max-dir-recursion=5 -l /var/log/clamav-var.log
```

### 2.扫描报告说明

```text
----------- SCAN SUMMARY -----------
Known viruses: 9141451                  #已知病毒
Engine version: 0.102.4                 #软件版本
Scanned directories: 498                #扫描目录
Scanned files: 738                      #扫描文件
Infected files: 4                       #感染文件!!!
Data scanned: 530.25 MB                 #扫描数据
Data read: 14131.60 MB (ratio 0.04:1)   #数据读取
Time: 203.805 sec (3 m 23 s)            #扫描用时
```

### 3.查看病毒文件

```text
cat /var/log/clamav-bin.log | grep "FOUND"
```