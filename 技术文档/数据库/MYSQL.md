### 恢复mysql-bin.000001

```sh
进入mysql:
	show variables like 'log_%';查看日志文件位置,然后进入日志文件位置执行一下命令
/usr/bin/mysqlbinlog --start-datetime="2013-03-01 00:00:00" --stop-datetime="2029-03-21 23:59:59" /var/lib/mysql/mysql-bin.000002 -r  tesst.sql

```

### 授权和密码规则

```mysql
set global validate_password_policy=0; //关闭密码规则
select @@validate_password_length; //查询密码设置长度
SHOW VARIABLES LIKE 'validate_password%';
　　001、validate_password_policy 这个参数用于控制validate_password的验证策略 0-->low  1-->MEDIUM  2-->strong。
　　002、validate_password_length密码长度的最小值(这个值最小要是8)。
　　003、validate_password_number_count 密码中数字的最小个数。
　　004、validate_password_mixed_case_count大小写的最小个数。
　　005、validate_password_special_char_count 特殊字符的最小个数。
　　006、validate_password_dictionary_file 字典文件
　　
// 创建用户
CREATE USER 'wxytest'@'%' IDENTIFIED BY 'wxy0715..';
CREATE USER 'wxytest'@'localhost' IDENTIFIED BY 'qqwang0715..';
grant all privileges on *.* to 'wxytest'@'%' identified by 'qqwang0715..' with grant option;
grant all privileges on *.* to 'wxytest'@'localhost' identified by 'qqwang0715..' with grant option;
flush privileges;

```

