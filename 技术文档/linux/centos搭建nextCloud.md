## 一、安装Apache

```
yum -y install httpd
systemctl start httpd
关闭防火墙
systemctl stop firewalld
```

## 二、安装PHP7.0 

 ```
接下来安装PHP， nextcloud是基于PHP的web程序
由于默认的YUM源无法升级PHP，所以需要添加第三方的YUM源，此处用RPM获取Centos7的YUM源：
rpm -Uvh https://mirror.webtatic.com/yum/el7/epel-release.rpm
rpm -Uvh https://mirror.webtatic.com/yum/el7/webtatic-release.rpm
安装PHP：
yum -y install php70w php70w-opcache php70w-xml php70w-mcrypt php70w-gd php70w-devel php70w-mysql php70w-intl php70w-mbstring php70w-fpm
查看PHP版本验证是否安装成功：# php -v
 ```



## 三、安装数据库

```
python脚本安装mariadb10.4
create database nextcloud;
create user 'wxy'@'%' identified by 'wxy';
grant all privileges on *.* to 'wxy'@'%' identified by 'wxy';
grant all privileges on *.* to 'wxy'@'localhost' identified by 'wxy';
flush privileges;
```



## 四、下载nextcloud

```
yum -y install wget
wget https://download.nextcloud.com/server/releases/nextcloud-15.0.4.zip
安装unzip工具：yum install unzip
进入/var/www/html目录：cd /var/www/html
解压nextcloud-15.0.4.zip文件：unzip nextcloud-15.0.4.zip
更改目录权限 chown apache nextcloud -Rf
更改目录权限 chmod 770 nextcloud -Rf
临时关闭SELinux： setenforce 0
systemctl restart httpd
访问:http://192.168.1.54/nextcloud/
```

